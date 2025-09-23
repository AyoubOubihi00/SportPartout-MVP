import { useEffect, useState, useMemo } from "react"
import { Map, Marker } from "pigeon-maps"
import { osm } from "pigeon-maps/providers"
import useGeolocalisation from "../../hooks/useGeolocalisation"
import { clubs } from "../../mock/club"
import ClubCard from "../ClubCard"
import Supercluster, {
    ClusterFeature,
    PointFeature,
} from "supercluster"

export default function MapView() {


    // Récupération de la position de l'utilisateur
    const { userPos } = useGeolocalisation()
    // Centre de la carte : position de l'utilisateur ou Paris par défaut
    const [center, setCenter] = useState<[number, number]>([48.8566, 2.3522])
    const [zoom, setZoom] = useState(12)
    const [bounds, setBounds] = useState<[number, number, number, number] | null>(null)
    const [selectedClub, setSelectedClub] = useState<number | null>(null)


    const points: PointFeature<any>[] = useMemo(
        () => clubs.map(club => ({
            type: "Feature" as const,
            properties: { cluster: false, clubId: club.id, name: club.name },
            geometry: { type: "Point" as const, coordinates: [club.position[1], club.position[0]] },
        })),
        []
    )

    const clusterIndex = useMemo(() => {
        return new Supercluster({
            radius: 90,
            maxZoom: 20,
        }).load(points)
    }, [points])

    const clusters = useMemo(() => {
        if (!bounds) return []
        return clusterIndex.getClusters(bounds, zoom)
    }, [clusterIndex, bounds, zoom])



    // Met à jour le centre lorsque la position de l'utilisateur change
    useEffect(() => {
        if (userPos) {
            setCenter(userPos)
        }
    }, [userPos])

    return (
        <div style={{ width: "100vw", height: "100vh", background: "#fff", overflow: "hidden" }}>
            <Map
                provider={osm}
                center={center}
                zoom={zoom}
                onBoundsChanged={({ center, zoom, bounds }) => {
                    setCenter(center)
                    setZoom(zoom)
                    setBounds([bounds.sw[1], bounds.sw[0], bounds.ne[1], bounds.ne[0]])
                }}
                animate={true}
                zoomSnap={false}
                dprs={[1, 2]}
                minZoom={2}
                maxZoom={50}
            >
                {userPos && (
                    <Marker
                        width={70}
                        anchor={userPos}
                        onClick={() => setCenter(userPos)}
                    />
                )}


                {clusters.map((cluster, index) => {
                    const [lng, lat] = cluster.geometry.coordinates
                    const { cluster: isCluster, point_count: pointCount } = cluster.properties as any

                    if (isCluster) {
                        return (
                            <Marker
                                key={`cluster-${cluster.id}-${index}`}
                                anchor={[lat, lng]}
                                width={30 + (pointCount / points.length) * 20}
                                onClick={() => {
                                    const expansionZoom = Math.min(
                                        clusterIndex.getClusterExpansionZoom(cluster.id as number),
                                        20
                                    )
                                    setCenter([lat, lng])
                                    setZoom(expansionZoom)
                                }}
                            >
                                <div
                                    style={{
                                        width: "30px",
                                        height: "30px",
                                        borderRadius: "50%",
                                        background: "rgba(0,150,255,0.7)",
                                        display: "flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        color: "#fff",
                                        fontSize: "14px",
                                        fontWeight: "bold",
                                    }}
                                >
                                    {pointCount}
                                </div>
                            </Marker>
                        )
                    }

                    // Marker individuel (club)
                    return (
                        <Marker
                            key={`club-${cluster.properties.clubId}`}
                            anchor={[lat, lng]}
                            width={50}
                            color="red"
                            onClick={() => setSelectedClub(cluster.properties.clubId)}
                        />
                    )
                })}
            </Map>

            <div style={{ position: "absolute", bottom: 20, left: 20 }}>
                {selectedClub && (
                    <ClubCard
                        {...clubs.find((club) => club.id === selectedClub)!}
                        onJoin={() => alert("Rejoindre le club")}
                        onDetails={() => alert("Voir les détails du club")}
                    />
                )}
            </div>
        </div>
    )
}
