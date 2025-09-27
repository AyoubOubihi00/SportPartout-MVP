import MapView from "./components/MapView";
import PWABadge from "./components/PWABadge";
import NavBar from "./components/NavBar/NavBar";
import { SearchFilter } from "./components/SearchFilter/SearchFilter";


function App() {
  return (
    <>
    <div className="fixed top-4 left-1/2 transform -translate-x-1/2 z-50 w-full max-w-md px-4">
      <SearchFilter />
    </div>
      <MapView />
      <PWABadge />
      <div className="fixed bottom-0 left-0 w-full z-50 flex justify-center">
        <div className="w-full max-w-md">
          <NavBar />
        </div>
      </div>
    </>
  );
}

export default App;