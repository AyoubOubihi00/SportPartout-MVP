import { useEffect, useState, useMemo } from "react"
import { Map, Marker } from "pigeon-maps"
import { osm } from "pigeon-maps/providers"
import useGeolocalisation from "../../hooks/useGeolocalisation"
import { mapCenterService } from '../../services/mapCenterService';
import { clubs } from "../../mock/club"
import ClubCard from "../ClubCard"
import { LocateFixed } from "lucide-react"
import Supercluster, { PointFeature } from "supercluster"
import { Button, Card, Sheet, IconButton } from "@mui/joy"
import { useTheme } from '@mui/joy/styles';

export default function MapView() {
    const theme = useTheme();
    const [manualCenter, setManualCenter] = useState(false);
    const [center, setCenter] = useState<[number, number]>([48.8566, 2.3522])
    const [zoom, setZoom] = useState(12)
    const [bounds, setBounds] = useState<[number, number, number, number] | null>(null)
    const [selectedClub, setSelectedClub] = useState<number | null>(null)
    const [isCardClosing, setIsCardClosing] = useState(false)

    const { userPos } = useGeolocalisation()

    useEffect(() => {
        const unsubscribe = mapCenterService.onCenterChange((newCenter) => {
            setCenter(newCenter);
            setManualCenter(true);
        });
        return unsubscribe;
    }, []);

    const points: PointFeature<any>[] = useMemo(
        () =>
            clubs.map((club) => ({
                type: "Feature" as const,
                properties: { cluster: false, clubId: club.id, name: club.name },
                geometry: { type: "Point" as const, coordinates: [club.position[1], club.position[0]] },
            })),
        []
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
                <Map
                provider={osm}
                center={center}
                zoom={zoom}
                onClick={() => {
                    // trigger animated close if card open
                    if (selectedClub !== null) {
                        setIsCardClosing(true)
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
                                // cancel any closing and open selected club (or toggle)
                                setIsCardClosing(false)
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
                    size="md"
                    sx={{
                        position: "fixed",
                        top: 64,
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

            {(selectedClub !== null || isCardClosing) && (
                <Card
                    sx={{
                        position: "fixed",
                        left: 12,
                        right: 12,
                        bottom: "calc(env(safe-area-inset-bottom, 0px) + 12px)",
                        zIndex: 60,
                        maxWidth: 560,
                        margin: "0 auto",
                        p: 0,
                        background: 'transparent',
                        boxShadow: 'none',
                        border: 'none',
                    }}
                    onClick={(e) => e.stopPropagation()}
                >
                    <ClubCard
                        {...clubs.find((c) => c.id === selectedClub)!}
                        onJoin={() => alert("Rejoindre le club")}
                        onDetails={() => alert("Voir les détails du club")}
                        closing={isCardClosing}
                        onCloseComplete={() => {
                            setIsCardClosing(false)
                            setSelectedClub(null)
                        }}
                        
                    />
                </Card>
            )}
        </Sheet>
    )
}
