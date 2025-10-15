import LoginRoute from "./src/pages/login";
import { SignupForm } from "./src/components/Connection-Subscription/SignupForm";
import { ResetPassword } from "./src/components/Connection-Subscription/ResetPassword";
import { ForgotPassword } from "./src/components/Connection-Subscription/ForgotPassword";
import MapView from "./src/components/MapView/MapView";
export default class Routes {
  apply(routeHandler) {
    const routes = [
      ...LoginRoute, // Ajoute la route de connexion
      ...SignupForm,
      ...ForgotPassword,
      ...ResetPassword,
      ...MapView,
    ];

    routeHandler.hooks.initRoutes.tapPromise("AppRoutes", async () => {
      routeHandler.addRoutes(routes);
    });
  }
}
