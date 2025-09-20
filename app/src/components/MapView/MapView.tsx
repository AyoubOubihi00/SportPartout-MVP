import { useEffect, useState } from "react"
import { Map, Marker } from "pigeon-maps"
import { osm } from "pigeon-maps/providers"
import useGeolocalisation from "../../hooks/useGeolocalisation"

export default function MapView() {


    // Récupération de la position de l'utilisateur
    const { userPos } = useGeolocalisation()
    // Centre de la carte : position de l'utilisateur ou Paris par défaut
    const [center, setCenter] = useState<[number, number]>([48.8566, 2.3522])

    // Met à jour le centre lorsque la position de l'utilisateur change
    useEffect(() => {
        if (userPos) {
            setCenter(userPos)
        }
    }, [userPos])

    return (
        <div style={{ width: "100vw", height: "100vh", background: "#fff", overflow:"hidden" }}>
            <Map
                provider={osm}
                center={center}
                zoom={18}
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
            </Map>
        </div>
    )
}
