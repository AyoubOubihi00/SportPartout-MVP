import React from "react";
import "./ClubCard.css"
import type  { Club } from "../../model/club";


type ClubCardProps = Club & {
    onJoin: () => void;
    onDetails: () => void;
};

export default function ClubCard({ name, sport , distance, notation, addresse, members, onJoin, onDetails,
}: ClubCardProps) {
    return (
        <div className="club-card">
            <div className="club-card-info">
                <div className="club-card-header">
                    <h3>{name}</h3>
                    <span className="distance">{distance} km</span>
                </div>

                <span className="sport">{sport}</span>

                <div className="notation">
                    <span>Notation: {notation} / 5</span>
                </div>

                <div className="addresse">
                    <span>Addresse: {addresse}</span>
                </div>
                <div className="members">
                    <span>Members: {members}</span>
                </div>
            </div>

            <div className="actions">
                <button className="join" onClick={onJoin}>Rejoindre</button>
                <button className="details" onClick={onDetails}>Voir plus</button>
            </div>
        </div>
    );
}