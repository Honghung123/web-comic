import React from "react";

export default function Content({ children }) {
    return (
        <>
            <div style={{ height: "300px" }}>{children}</div>
        </>
    );
}
