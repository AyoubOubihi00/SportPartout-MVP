import { useEffect, useState, useMemo } from "react"
import { Map, Marker } from "pigeon-maps"
import { osm } from "pigeon-maps/providers"
import useGeolocalisation from "../../hooks/useGeolocalisation"
import { mapCenterService } from '../../services/mapCenterService';
import { clubs } from "../../mock/club"
import { SearchFilter } from "../SearchFilter/SearchFilter"
import ClubCard from "../ClubCard"
import ClubDetails from "../ClubDetails"
import BottomSheet from "../BottomSheet"
import { LocateFixed } from "lucide-react"
import { Button, Sheet, IconButton, Box } from "@mui/joy"
import { useTheme } from '@mui/joy/styles';
import Supercluster, {
    PointFeature,
} from "supercluster"
//import { useNavigate } from "react-router-dom";

export default function MapView() {
    const theme = useTheme();
    const [manualCenter, setManualCenter] = useState(false);
    const [selectedSports, setSelectedSports] = useState<any[]>([]);

    // Récupération de la position de l'utilisateur
    const { userPos } = useGeolocalisation()
    // Centre de la carte : position de l'utilisateur ou Paris par défaut
    const [center, setCenter] = useState<[number, number]>([48.8566, 2.3522])
    const [zoom, setZoom] = useState(12)
    const [bounds, setBounds] = useState<[number, number, number, number] | null>(null)
    const [selectedClub, setSelectedClub] = useState<string | null>(null)
    const [showDetails, setShowDetails] = useState(false)

    useEffect(() => {
        const unsubscribe = mapCenterService.onCenterChange((newCenter) => {
            setCenter(newCenter);
            setManualCenter(true);
        });
        return unsubscribe;
    }, []);

    // Filtrage des clubs selon les sports sélectionnés
    const filteredClubs = useMemo(() => {
        if (!selectedSports || selectedSports.length === 0) return clubs;
        return clubs.filter(club =>
            selectedSports.some(sel => sel.name === club.sport || sel.typeName === club.sport)
        );
    }, [selectedSports]);

    const points: PointFeature<any>[] = useMemo(
        () =>
            filteredClubs.map((club) => ({
                type: "Feature" as const,
                properties: { cluster: false, clubId: club.id, name: club.name },
                geometry: { type: "Point" as const, coordinates: [club.position[1], club.position[0]] },
            })),
        [filteredClubs]
    )

    const clusterIndex = useMemo(
        () =>
            new Supercluster({
                radius: 90,
                maxZoom: 20,
            }).load(points),
        [points]
    )

    const clusters = useMemo(() => {
        if (!bounds) return []
        return clusterIndex.getClusters(bounds, zoom)
    }, [clusterIndex, bounds, zoom])

    useEffect(() => {
        if (userPos && !manualCenter) {
            setCenter(userPos)
        }
    }, [userPos, manualCenter])

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
            <Box sx={{ position: 'absolute', top: 24, left: 0, width: '100%', zIndex: 100, px: 2 }}>
                <SearchFilter onSportsChange={setSelectedSports} />
            </Box>
                <Map
                provider={osm}
                center={center}
                zoom={zoom}
                onClick={() => {
                    // Close any open sheet
                    if (selectedClub !== null) {
                        setShowDetails(false)
                        setSelectedClub(null)
                    }
                }}
                onBoundsChanged={({ center, zoom, bounds }) => {
                    setCenter(center as [number, number])
                    setZoom(zoom)
                    setBounds([bounds.sw[1], bounds.sw[0], bounds.ne[1], bounds.ne[0]])
                }}
                animate
                zoomSnap={false}
                dprs={[1, 2]}
                minZoom={2}
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

                {clusters.map((cluster: any, index: number) => {
                    const [lng, lat] = cluster.geometry.coordinates
                    const { cluster: isCluster, point_count: pointCount } = cluster.properties as any

                    if (isCluster) {
                        return (
                            <Marker key={`cluster-${cluster.id}-${index}`} anchor={[lat, lng]}>
                                <Button
                                    variant="solid"
                                    color="primary"
                                    size="sm"
                                    onClick={(e) => {
                                        e.stopPropagation()
                                        const baseZoom = clusterIndex.getClusterExpansionZoom(cluster.id as number)
                                        const expansionZoom = Math.min(baseZoom + 1, 20)
                                        setCenter([lat, lng])
                                        setZoom(expansionZoom)
                                        setSelectedClub(null)
                                    }}
                                    sx={{
                                        width: 32,
                                        height: 32,
                                        borderRadius: "50%",
                                        background: theme.palette.mode === 'dark'
                                            ? 'linear-gradient(135deg, #0f172a, #1e293b)'
                                            : 'linear-gradient(135deg, #f8fafc, #e2e8f0)',
                                        color: theme.palette.mode === 'dark' ? '#f8fafc' : '#1e293b',
                                        border: '2px solid white',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        fontSize: 14,
                                        fontWeight: 700,
                                        pointerEvents: 'auto',
                                        cursor: 'pointer',
                                        touchAction: 'manipulation',
                                        boxShadow: 'none',
                                        outline: 'none',
                                        transition: 'background 0.2s',
                                        '&:focus': {
                                            border: 'none',
                                            outline: 'none',
                                            boxShadow: 'none',
                                        },
                                        '&:active': {
                                            border: 'none',
                                            outline: 'none',
                                            boxShadow: 'none',
                                            background: theme.palette.mode === 'dark'
                                                ? 'linear-gradient(135deg, #1e293b, #334155)'
                                                : 'linear-gradient(135deg, #e2e8f0, #cbd5e1)',
                                        },
                                    }}
                                >
                                    {pointCount}
                                </Button>
                            </Marker>
                        )
                    }

                    return (
                        <Marker
                            key={`club-${cluster.properties.clubId}`}
                            anchor={[lat, lng]}
                            width={42}
                            color={'#0f172a'}
                                onClick={({ event }) => {
                                event.stopPropagation()
                                const newId = cluster.properties.clubId
                                // open selected club
                                setSelectedClub((prev) => (prev === newId ? newId : newId))
                            }}
                        />
                    )
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
                        background: theme.palette.mode === 'dark'
                            ? 'linear-gradient(135deg, #0f172a, #1e293b)'
                            : 'linear-gradient(135deg, #f8fafc, #e2e8f0)',
                        color: theme.palette.mode === 'dark' ? '#f8fafc' : '#1e293b',
                        boxShadow: 'none',
                        border: 'none',
                        outline: 'none',
                        transition: 'background 0.2s',
                        '&:focus': {
                            border: 'none',
                            outline: 'none',
                            boxShadow: 'none',
                        },
                        '&:active': {
                            border: 'none',
                            outline: 'none',
                            boxShadow: 'none',
                            background: theme.palette.mode === 'dark'
                                ? 'linear-gradient(135deg, #1e293b, #334155)'
                                : 'linear-gradient(135deg, #e2e8f0, #cbd5e1)',
                        },
                    }}
                >
                    <LocateFixed />
                </IconButton>
            )}

            <BottomSheet
                open={selectedClub !== null}
                onClose={() => { setShowDetails(false); setSelectedClub(null) }}
                initialHeight={showDetails ? 560 : 420}
            >
                {selectedClub && !showDetails && (
                    <ClubCard
                        {...clubs.find((c) => c.id === selectedClub)!}
                        onDetails={() => setShowDetails(true)}
                        {...clubs.find((club) => club.id === selectedClub)!}
                            onJoin={() => { alert("Join club functionality to be implemented");
                                /*
                                if (!user) {
                                    // Pas connecté → aller à la page login, et prévoir de revenir ici
                                    navigate("/login", { state: { redirectTo: `/clubs/${selectedClub}/join` } });
                                } else {
                                    // Connecté → aller directement à la page rejoindre
                                    navigate(`/clubs/${selectedClub}/join`);
                                }
                                    */
                            }}
                    />
                )}
                {selectedClub && showDetails && (
                    <ClubDetails club={clubs.find((c) => c.id === selectedClub)!} />
                )}
            </BottomSheet>
        </Sheet>
    )
}
