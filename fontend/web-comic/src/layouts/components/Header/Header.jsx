import React from 'react';
import AutoStoriesIcon from '@mui/icons-material/AutoStories';
import { Link } from 'react-router-dom';

import SearchBox from '../../../components/SearchBox';

function Header() {
    return (
        <div
            className="flex justify-end lg:justify-center relative"
            style={{
                background: 'linear-gradient(to right, rgba(155, 86, 244, 0.7), rgba(247, 162, 249, 0.5))',
            }}
        >
            <SearchBox></SearchBox>
            <Link
                to={'/'}
                className="absolute top-3 left-3 text-white flex items-center justify-center p-2 cursor-pointer"
            >
                <AutoStoriesIcon sx={{ fontSize: 50 }} />
                <div className="text-3xl ml-4 hidden sm:block">WebComic</div>
            </Link>
        </div>
    );
}

export default Header;
