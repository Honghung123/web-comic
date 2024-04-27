import React from 'react';

export default function Content({ children }) {
    return (
        <>
            <div style={{ minHeight: '550px' }}>{children}</div>
        </>
    );
}
