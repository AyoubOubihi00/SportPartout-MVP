import MapView from "../MapView";
import NavBar from "../NavBar/NavBar";
import PWABadge from "../PWABadge";

export function HomePage() {
  return (
    <div>
      <MapView />
      <PWABadge />
      <div className="fixed bottom-0 left-0 w-full z-50 flex justify-center">
        <div className="w-full max-w-md">
          <NavBar />
        </div>
      </div>
    </div>
  );
}
