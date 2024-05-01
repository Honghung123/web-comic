import axios from 'axios';
import { useContext, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';

import { Context } from '../../GlobalContext';

function ListBook() {
    const { servers, serversDispatch, genre, keyword } = useContext(Context);

    const [comicsData, setComicsData] = useState({});

    useEffect(() => {
        if (servers && servers.length > 0) {
            console.log('use effce');
            const server_id = servers.find((server) => server.priority === 1).id;
            axios
                .get(`http://localhost:8080/api/v1/comic/search`, {
                    params: {
                        server_id,
                        page: 1,
                        genres: genre,
                        keyword,
                    },
                })
                .then((response) => {
                    if (response.data.statusCode == 200) {
                        console.log('response data: ', response.data.data);
                        setComicsData({
                            comics: response.data.data,
                            pagination: response.data.pagination,
                        });
                    }
                })
                .catch((error) => {
                    //thong bao loi
                    console.log(error);
                });
        }
    }, [servers, genre]);

    console.log('commic data: ', comicsData);

    return (
        <div className="bg-gray-100 min-h-screen mt-8">
            <h2 className="text-3xl font-semibold underline">Tim kiem cho: {keyword || genre}</h2>

            <div className="flex flex-wrap min-h-full" style={{ marginLeft: '-1rem', marginRight: '-1rem' }}>
                {comicsData.comics &&
                    comicsData.comics.map((comic) => {
                        return (
                            <div key={comic.tagId} className="lg:w-1/6 md:w-1/4 sm:w-1/3 w-1/2 px-4 mt-8">
                                <Link
                                    to="/info"
                                    className="block comic-item w-full h-full relative hover:transform hover:scale-105 transition-all duration-300"
                                >
                                    <img className="w-full h-full object-cover" src={comic.image} alt="" />
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
                            </div>
                        );
                    })}
            </div>
        </div>
    );
}

export default ListBook;
