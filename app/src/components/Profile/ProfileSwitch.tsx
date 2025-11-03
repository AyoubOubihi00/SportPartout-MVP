import { useSelector } from "react-redux";
import type { RootState } from "../../store";
import OwnerProfile from "./OwnerProfile/OwnerProfile";
import { MemberProfile } from "./MemberProfile/MemberProfile";
import ProfileLayout from "./ProfileLayout";

export default function ProfileSwitch() {
  const roles = useSelector((state: RootState) => state.roles.roles);
  const viewMode = useSelector((state: RootState) => state.roles.viewMode);
  const ownerCapable = roles.includes("OWNER");
  const showOwner = ownerCapable && viewMode === "OWNER";
  return (
    <ProfileLayout>
      {showOwner ? <OwnerProfile /> : <MemberProfile />}
    </ProfileLayout>
  );
}
