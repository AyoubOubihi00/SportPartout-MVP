// src/App.tsx
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { LoginForm } from "./components/Connection-Subscription/LoginForm";
import { SignupForm } from "./components/Connection-Subscription/SignupForm";
import { ResetPassword } from "./components/Connection-Subscription/ResetPassword";
import { ResetPasswordSuccess } from "./components/Connection-Subscription/ResetPasswordSuccess";
import { ForgotPassword } from "./components/Connection-Subscription/forgotPassword";
import { HomePage } from "./components/HomePage/HomePage";
function App() {
  return (
    <Router>
 
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginForm />} />
        <Route path="/signup" element={<SignupForm />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/resetpassword" element={<ResetPassword />} />
        <Route path="/resetpasswordsuccess" element={<ResetPasswordSuccess />} />

      </Routes>


    </Router>
  );
}

export default App;
