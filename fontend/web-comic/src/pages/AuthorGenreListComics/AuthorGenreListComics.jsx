import { useContext } from 'react';

import { Context } from '../../components/GlobalContext';
import ListComicsV2 from '../../components/ListComicsV2';

function AuthorGenreListComics() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('author-genre');

    return (
        <div className="px-4 py-8">
            <ListComicsV2 />
        </div>
    );
}

export default AuthorGenreListComics;
