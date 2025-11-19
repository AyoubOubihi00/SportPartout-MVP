import { useEffect, useState, useMemo, useRef } from "react";
import { Map, Marker } from "pigeon-maps";
import useGeolocalisation from "../../hooks/useGeolocalisation";
import { mapCenterService } from "../../services/mapCenterService";
import { SearchFilter } from "../SearchFilter/SearchFilter";
import ClubCard from "../ClubCard";
import ClubDetails from "../ClubDetails";
import BottomSheet from "../BottomSheet";
import { osm } from "pigeon-maps/providers";
import { LocateFixed } from "lucide-react";
import {
  Button,
  Sheet,
  IconButton,
  Box,
  LinearProgress,
  Typography,
} from "@mui/joy";
import { useTheme } from "@mui/joy/styles";
import Supercluster, { PointFeature } from "supercluster";
import { Club } from "../../model/Club";
import { fetchClubById } from "../../api/club";
import { estimateMapRadius } from "../../services/mapRadiusService";
import { getClubsNear } from "../../services/clubService";
import { haversineDistance } from "../../services/distanceService";
import { useAuth } from "../shared/useAuth";
import { useBubble } from "../shared/useBubble";
import { getMe } from "../../services/auth/authService";
import {
  subscribeToClub,
  type UserInput,
} from "../../services/membershipService";
import { useNavigate } from "react-router-dom";
import { SportType } from "../../model/SportType";

interface ClubFeatureProperties {
  cluster: boolean;
  clubId: string;
  name: string;
  point_count?: number;
  point_count_abbreviated?: number;
}

type ClusterFeature = Supercluster.ClusterFeature<ClubFeatureProperties>;
type PointFeatureType = PointFeature<ClubFeatureProperties>;
type ClusterOrPoint = ClusterFeature | PointFeatureType;

export default function MapView() {
  const [clubs, setClubs] = useState<Club[]>([]);
  const theme = useTheme();
  const { token } = useAuth();
  const { showSuccessBubble, showErrorBubble } = useBubble();
  const navigate = useNavigate();
  const [manualCenter, setManualCenter] = useState(false);
  const [selectedSports, setSelectedSports] = useState<SportType[]>([]);
  // State pour les coins de la carte
  const [
    ,
    //mapCorners
    setMapCorners,
  ] = useState<{
    topLeft: [number, number] | null;
    bottomRight: [number, number] | null;
  }>({ topLeft: null, bottomRight: null });

  // Récupération de la position de l'utilisateur
  const { userPos } = useGeolocalisation();
  // Centre de la carte : position de l'utilisateur ou Paris par défaut
  const [center, setCenter] = useState<[number, number]>([48.8566, 2.3522]);
  const [zoom, setZoom] = useState(12);
  const [bounds, setBounds] = useState<[number, number, number, number] | null>(
    null,
  );
  const [selectedClub, setSelectedClub] = useState<string | null>(null);
  const [showDetails, setShowDetails] = useState(false);
  const [selectedClubDetails, setSelectedClubDetails] = useState<Club | null>(
    null,
  );
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [detailsError, setDetailsError] = useState<string | null>(null);
  const detailRequestRef = useRef(0);
  const [isOwner, setIsOwner] = useState(false);

  const resetSelection = () => {
    detailRequestRef.current += 1;
    setShowDetails(false);
    setSelectedClub(null);
    setSelectedClubDetails(null);
    setDetailsLoading(false);
    setDetailsError(null);
  };

  const selectClub = (clubId: string) => {
    detailRequestRef.current += 1;
    setSelectedClub(clubId);
    setShowDetails(false);
    setSelectedClubDetails(null);
    setDetailsLoading(false);
    setDetailsError(null);
  };

  const handleShowDetails = async (clubId: string) => {
    const fallback = clubs.find((club) => club.id === clubId) ?? null;
    setShowDetails(true);
    setDetailsError(null);
    setSelectedClubDetails(fallback);
    setDetailsLoading(true);
    const requestId = ++detailRequestRef.current;
    try {
      const detailed = await fetchClubById(clubId, {
        distanceKm: fallback?.distance,
        notation: fallback?.notation,
        members: fallback?.members,
      });
      if (detailRequestRef.current !== requestId) return;
      setSelectedClubDetails(detailed ?? fallback);
    } catch (err) {
      if (detailRequestRef.current !== requestId) return;
      const message = err instanceof Error ? err.message : String(err);
      setDetailsError(message);
      setSelectedClubDetails(fallback);
    } finally {
      if (detailRequestRef.current === requestId) {
        setDetailsLoading(false);
      }
    }
  };

  useEffect(() => {
    const unsubscribe = mapCenterService.onCenterChange((newCenter) => {
      setCenter(newCenter);
      setManualCenter(true);
    });
    return unsubscribe;
  }, []);

  const filteredClubs = useMemo(() => {
    if (!selectedSports || selectedSports.length === 0) return clubs;
    return clubs.filter((club) =>
      selectedSports.some(
        (sel) =>
          sel.name === club.sportType?.id || sel.id === club.sportType?.id,
      ),
    );
  }, [selectedSports, clubs]);

  const points: PointFeatureType[] = useMemo(
    () =>
      filteredClubs.map((club: Club) => ({
        type: "Feature" as const,
        properties: {
          cluster: false,
          clubId: club.id,
          name: club.name,
        },
        geometry: {
          type: "Point" as const,
          coordinates: [club.position[1], club.position[0]],
        },
      })),
    [filteredClubs],
  );

  const clusterIndex = useMemo(
    () =>
      new Supercluster<ClubFeatureProperties>({
        radius: 90,
        maxZoom: 20,
      }).load(points),
    [points],
  );

  const clusters: ClusterOrPoint[] = useMemo(() => {
    if (!bounds) return [];
    return clusterIndex.getClusters(bounds, zoom) as ClusterOrPoint[];
  }, [clusterIndex, bounds, zoom]);

  useEffect(() => {
    if (userPos && !manualCenter) {
      setCenter(userPos);
    }
  }, [userPos, manualCenter]);

  useEffect(() => {
    if (!token) {
      setIsOwner(false);
      return;
    }
    let cancelled = false;
    getMe(token)
      .then((profile) => {
        if (cancelled) return;
        setIsOwner(profile?.role?.toUpperCase() === "OWNER");
      })
      .catch(() => {
        if (!cancelled) {
          setIsOwner(false);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [token]);

  const currentClub =
    selectedClub !== null
      ? (clubs.find((club) => club.id === selectedClub) ?? null)
      : null;

  return (
    <Sheet
      sx={{
        position: "relative",
        width: "100vw",
        height: "100dvh",
        overflow: "hidden",
        bgcolor: "background.body",
      }}
    >
      <Box
        sx={{
          position: "absolute",
          top: 24,
          left: 0,
          width: "100%",
          zIndex: 100,
          px: 2,
        }}
      >
        <SearchFilter onSportsChange={setSelectedSports} />
      </Box>

      <Map
        provider={osm}
        center={center}
        zoom={zoom}
        onClick={() => {
          if (selectedClub !== null) {
            resetSelection();
          }
        }}
        onBoundsChanged={({ center, zoom, bounds }) => {
          setCenter(center as [number, number]);
          setZoom(zoom);
          setBounds([bounds.sw[1], bounds.sw[0], bounds.ne[1], bounds.ne[0]]);
          // Détection coins :
          // Coin haut-gauche (nord-ouest)
          const topLeft: [number, number] = [bounds.ne[0], bounds.sw[1]];
          // Coin bas-droit (sud-est)
          const bottomRight: [number, number] = [bounds.sw[0], bounds.ne[1]];
          setMapCorners({ topLeft, bottomRight });

          // Appel API clubsNear
          // On prend le centre de la carte et le rayon estimé (distance centre -> coin haut-gauche)
          const lat = (center as [number, number])[0];
          const lng = (center as [number, number])[1];
          // Rayon en km (distance entre centre et coin haut-gauche)
          const radiusKm = estimateMapRadius([lat, lng], topLeft);
          getClubsNear(lat, lng, radiusKm)
            .then((data) => {
              const mapped = data.map((club) => {
                const clubPosition: [number, number] = [
                  club.latitude,
                  club.longitude,
                ];
                const userPosition = userPos ?? center;
                const distance = haversineDistance(userPosition, clubPosition);

                return {
                  id: club.id,
                  name: club.name,
                  sportType: { id: club.typeIdSport, name: club.typeNameSport },
                  distance: Number(distance.toFixed(2)),
                  notation: club.notation ?? 0,
                  addresse: `${club.address}${
                    club.city ? ", " + club.city : ""
                  }`,
                  members: club.memberCount ?? 0,
                  position: clubPosition,
                };
              });
              setClubs(mapped);
            })
            .catch((err) => {
              console.error("Erreur clubsNear:", err);
            });
        }}
        animate
        zoomSnap={false}
        dprs={[1, 2]}
        minZoom={6}
        maxZoom={50}
      >
        {userPos && (
          <Marker anchor={userPos}>
            <Sheet
              sx={{
                width: 20,
                height: 20,
                bgcolor: "primary.500",
                borderRadius: "50%",
                border: "4px solid white",
                boxShadow: "sm",
              }}
            />
          </Marker>
        )}

        {clusters.map((cluster, index) => {
          const [lng, lat] = cluster.geometry.coordinates;

          if ("cluster" in cluster.properties && cluster.properties.cluster) {
            // 🎯 Cluster
            const clusterFeature = cluster as ClusterFeature;
            const pointCount = clusterFeature.properties.point_count ?? 0;

            return (
              <Marker
                key={`cluster-${cluster.id}-${index}`}
                anchor={[lat, lng]}
              >
                <Button
                  variant="solid"
                  color="primary"
                  size="sm"
                  onClick={(e) => {
                    e.stopPropagation();
                    const baseZoom = clusterIndex.getClusterExpansionZoom(
                      cluster.id as number,
                    );
                    const expansionZoom = Math.min(baseZoom + 1, 20);
                    setCenter([lat, lng]);
                    setZoom(expansionZoom);
                    resetSelection();
                  }}
                  sx={{
                    width: 32,
                    height: 32,
                    borderRadius: "50%",
                    background:
                      theme.palette.mode === "dark"
                        ? "linear-gradient(135deg, #0f172a, #1e293b)"
                        : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
                    color:
                      theme.palette.mode === "dark" ? "#f8fafc" : "#1e293b",
                    border: "2px solid white",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontSize: 14,
                    fontWeight: 700,
                    pointerEvents: "auto",
                    cursor: "pointer",
                    touchAction: "manipulation",
                    boxShadow: "none",
                    outline: "none",
                    transition: "background 0.2s",
                    "&:focus": {
                      border: "none",
                      outline: "none",
                      boxShadow: "none",
                    },
                    "&:active": {
                      border: "none",
                      outline: "none",
                      boxShadow: "none",
                      background:
                        theme.palette.mode === "dark"
                          ? "linear-gradient(135deg, #1e293b, #334155)"
                          : "linear-gradient(135deg, #e2e8f0, #cbd5e1)",
                    },
                  }}
                >
                  {pointCount}
                </Button>
              </Marker>
            );
          } else {
            // 🎯 Point (Club)
            const pointFeature = cluster as PointFeatureType;
            const { clubId } = pointFeature.properties;

            return (
              <Marker
                key={`club-${clubId}`}
                anchor={[lat, lng]}
                width={48}
                color={selectedClub === clubId ? "#eab308" : "#0f172a"}
                onClick={({ event }) => {
                  event.stopPropagation();
                  selectClub(clubId);
                }}
                style={{
                  filter:
                    selectedClub === clubId
                      ? "drop-shadow(0 0 8px #eab308)"
                      : undefined,
                  zIndex: selectedClub === clubId ? 10 : undefined,
                  transition: "filter 0.15s, z-index 0.15s",
                }}
              />
            );
          }
        })}
      </Map>

      {userPos && (
        <IconButton
          onClick={() => setCenter(userPos)}
          variant="outlined"
          color="neutral"
          size="lg"
          sx={{
            position: "fixed",
            bottom: 128,
            right: 16,
            zIndex: 50,
            borderRadius: 3,
            background:
              theme.palette.mode === "dark"
                ? "linear-gradient(135deg, #0f172a, #1e293b)"
                : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
            color: theme.palette.mode === "dark" ? "#f8fafc" : "#1e293b",
            boxShadow: "none",
            border: "none",
            outline: "none",
            transition: "background 0.2s",
            "&:focus": {
              border: "none",
              outline: "none",
              boxShadow: "none",
            },
            "&:active": {
              border: "none",
              outline: "none",
              boxShadow: "none",
              background:
                theme.palette.mode === "dark"
                  ? "linear-gradient(135deg, #1e293b, #334155)"
                  : "linear-gradient(135deg, #e2e8f0, #cbd5e1)",
            },
          }}
        >
          <LocateFixed />
        </IconButton>
      )}

      <BottomSheet
        open={selectedClub !== null}
        onClose={resetSelection}
        initialHeight={showDetails ? 560 : 420}
      >
        {selectedClub && !showDetails && currentClub && (
          <ClubCard
            {...currentClub}
            disableJoin={isOwner}
            onDetails={() => handleShowDetails(currentClub.id)}
            onJoin={async () => {
              if (!selectedClub || isOwner) return;
              if (!token) {
                navigate("/login");
                return;
              }
              try {
                const me = await getMe(token);
                if (!me) {
                  navigate("/login");
                  return;
                }
                const input: UserInput = {
                  userId: me.id,
                  firstName: me.firstName,
                  lastName: me.lastName,
                  birthDate: new Date(me.birthDate).toISOString(),
                  email: me.email,
                  phoneNumber: me.phone || undefined,
                };
                const res = await subscribeToClub(selectedClub, input);
                if (res.success) {
                  showSuccessBubble?.("Inscription au club réussie");
                } else {
                  showErrorBubble?.(
                    res.message || "Erreur lors de l’inscription",
                  );
                }
              } catch (e) {
                const msg = e instanceof Error ? e.message : String(e);
                showErrorBubble?.(msg);
              }
            }}
          />
        )}

        {selectedClub &&
          showDetails &&
          ((selectedClubDetails ?? currentClub) ? (
            <Box
              sx={{
                background:
                  theme.palette.mode === "dark"
                    ? "linear-gradient(180deg, rgba(15,23,42,0.9), rgba(15,23,42,0.7))"
                    : "linear-gradient(180deg, #f7f9fc, #edf1f7)",
                borderRadius: 16,
                boxShadow:
                  theme.palette.mode === "dark"
                    ? "0 20px 40px rgba(0,0,0,0.4)"
                    : "0 24px 45px rgba(15,23,42,0.12)",
                p: { xs: 1.5, sm: 2 },
              }}
            >
              <ClubDetails
                club={(selectedClubDetails ?? currentClub)!}
                disableJoin={isOwner}
              />
            </Box>
          ) : (
            <Sheet
              variant="outlined"
              sx={{
                borderRadius: 16,
                p: 4,
                textAlign: "center",
                minHeight: 200,
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                gap: 2,
                bgcolor:
                  theme.palette.mode === "dark"
                    ? "neutral.900"
                    : "background.body",
              }}
            >
              <Typography
                level="body-md"
                sx={{ color: theme.palette.text.primary }}
              >
                {detailsError ??
                  (detailsLoading
                    ? "Chargement du club..."
                    : "Club introuvable.")}
              </Typography>
              {detailsLoading && (
                <LinearProgress
                  variant="soft"
                  color="primary"
                  sx={{ borderRadius: 8 }}
                />
              )}
            </Sheet>
          ))}
      </BottomSheet>
    </Sheet>
  );
}
