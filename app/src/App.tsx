// src/App.tsx
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { LoginForm } from "./components/Connection-Subscription/LoginForm";
import { SignupForm } from "./components/Connection-Subscription/SignupForm";
import { ResetPassword } from "./components/Connection-Subscription/ResetPassword";
import { ResetPasswordSuccess } from "./components/Connection-Subscription/ResetPasswordSuccess";
import { ForgotPassword } from "./components/Connection-Subscription/forgotPassword";
import { HomePage } from "./components/HomePage/HomePage";
import { MemberProfile } from "./components/Profile/MemberProfile/MemberProfile";
import { EditMemberProfile } from "./components/Profile/MemberProfile/EditMemberProfile";
import OwnerProfile from "./components/Profile/OwnerProfile/OwnerProfile";
import { BubbleProvider } from "./components/shared/SuccessBubbleContext";
import { AuthProvider } from "./components/shared/AuthContext";

function App() {
  return (
    <AuthProvider>
      <BubbleProvider>
        <Router>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginForm />} />
            <Route path="/signup" element={<SignupForm />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/resetpassword" element={<ResetPassword />} />
            <Route
              path="/resetpasswordsuccess"
              element={<ResetPasswordSuccess />}
            />
            <Route path="/memberprofile" element={<MemberProfile />} />
            <Route path="/editmemberprofile" element={<EditMemberProfile />} />
            <Route path="ownerprofile" element={<OwnerProfile />} />
          </Routes>
        </Router>
      </BubbleProvider>
    </AuthProvider>
  );
}

export default App;
