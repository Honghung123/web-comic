import React from "react";

export default function Form({ children, method, action, handleSubmit, className }) {
    const classes = `${className} form`;
    return (
        <>
            <form method={method} action={action} className={classes} onSubmit={handleSubmit}>
                {children}
            </form>
        </>
    );
}
