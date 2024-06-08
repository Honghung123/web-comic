import { Link } from 'react-router-dom';
import { useContext } from 'react';

import { Context } from '../GlobalContext';

function BookItem({ comic }) {
    const { servers } = useContext(Context);

    return (
        <Link
            to={`/info/${servers[0]?.id}/${comic.tagId}`}
            className="block comic-item w-full h-full relative shadow-lg overflow-hidden"
        >
            <img
                className="w-full h-full object-cover hover:transform hover:scale-110 transition-all duration-300"
                src={comic.image}
                onError={(e) => (e.target.src = comic.alternateImage)}
                alt="image"
            />
            <div className="w-full absolute bottom-0 bg-zinc-800/70 text-white text-center line-clamp-2 min-h-[48px]">
                {comic.title}
            </div>
        </Link>
    );
}

export default BookItem;
