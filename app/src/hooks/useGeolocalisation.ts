import { useEffect, useState } from "react";

export default function useGeolocalisation() {
  const [userPos, setUserPos] = useState<[number, number] | null>(null);

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const coords: [number, number] = [
          pos.coords.latitude,
          pos.coords.longitude,
        ];
        setUserPos(coords);
      },
      (err) => {
        console.error("Erreur de géolocalisation :", err);
      },
    );
  }, []);

  return { userPos };
}
