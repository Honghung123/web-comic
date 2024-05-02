import { Link } from 'react-router-dom';
function BookItem({ comic }) {
    return (
        <Link to={`/info/${comic.tagId}`} className="block comic-item w-full h-full relative shadow-lg overflow-hidden">
            <img
                className="w-full h-full object-cover hover:transform hover:scale-110 transition-all duration-300"
                src={comic.image}
                onError={e => e.target.src = comic.alternateImage}
                alt=""
            />
            <div
                className="w-full absolute bottom-0 bg-zinc-800/70 text-white text-center"
                style={{
                    height: 48,
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                }}
            >
                {comic.title}
            </div>
        </Link>
    );
}

export default BookItem;
