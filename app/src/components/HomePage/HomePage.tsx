
import MapView from "../MapView"
import NavBar from "../NavBar/NavBar"
import PWABadge from "../PWABadge"
import { SearchFilter

 } from "../SearchFilter/SearchFilter"

export function HomePage() {
    return (
        <div>
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
        </div>
    );
}
