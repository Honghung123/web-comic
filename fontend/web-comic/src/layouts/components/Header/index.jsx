import React from 'react';
import SearchBox from '../../../components/SearchBox';

export default function Header() {
    return (
        <div
            className="flex justify-center"
            style={{
                background: 'linear-gradient(to right, rgba(155, 86, 244, 0.7), rgba(247, 162, 249, 0.5))',
            }}
        >
            <SearchBox></SearchBox>
        </div>
    );
}
