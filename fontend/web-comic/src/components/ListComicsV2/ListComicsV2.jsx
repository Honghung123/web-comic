import { useContext, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { toast } from 'react-toastify';
import axios from 'axios';

import { Context } from '../../GlobalContext';
function ListComicsV2() {
    const location = useLocation();
    const { pathname } = location;
    const authorId = pathname.substring(pathname.lastIndexOf('/') + 1);
    const { servers } = useContext(Context);
    const [comicsData, setComicsData] = useState({});
    const [page, setPage] = useState(1);

    useEffect(() => {
        window.scrollTo(0, 0);
        setComicsData({});
        if (servers && servers.length > 0) {
            const server_id = servers.find((server) => server.priority === 1).id;
            axios
                .get(`http://localhost:8080/api/v1/comic/author/${authorId}`, {
                    params: {
                        server_id,
                        page,
                    },
                    headers: {
                        'crawler-size': servers.length,
                    },
                })
                .then((response) => {
                    console.log('response of list comics v2: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        setComicsData({
                            comics: responseData.data,
                            pagination: responseData.pagination,
                        });
                    } else {
                        // thong bao loi
                        console.log(responseData.message);
                        toast.error(responseData.message);
                    }
                })
                .catch((err) => {
                    //thong bao loi
                    console.log(err);
                    if (err.response?.status === 503) {
                        // back end update list servers
                        toast.error(err.response.data?.message, {
                            toastId: 503,
                            autoClose: false,
                        });
                    } else {
                        toast.error('Internal server error');
                    }
                });
        }
    }, [page, authorId]);

    return (
        <div className="min-h-96 p-2 mx-auto relative" style={{ maxWidth: 1200 }}>
            {comicsData.comics && (
                <>
                    <h2 className="text-3xl font-semibold underline underline-offset-8">
                        {comicsData.comics.length && `Tác giả ${comicsData.comics[0].author.name}`}
                    </h2>
                    <div className="divide-y">
                        {comicsData.comics.map((comic) => {
                            return (
                                <div className="my-2 py-2 flex items-center justify-between">
                                    <div className="flex items-center">
                                        <div className="w-52 h-32 overflow-hidden">
                                            <img
                                                className="w-full h-full object-cover hover:transform hover:scale-110 transition-all duration-300"
                                                src={comic.image}
                                                onError={(e) => {
                                                    e.target.src = comic.alternateImage;
                                                }}
                                                alt=""
                                            />
                                        </div>
                                        <div className="px-4">
                                            <h3 className="text-xl font-semibold">{comic.title}</h3>
                                            <div>{comic.author.name}</div>
                                            <div>
                                                The loai:{' '}
                                                {comic.genres.map((genre) => (
                                                    <span>{genre.label}</span>
                                                ))}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="lg:pr-8 hidden sm:block font-semibold text-purple-500 text-lg">
                                        {comic.totalChapter && `Chương ${comic.totalChapter}`}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </>
            )}
        </div>
    );
}

export default ListComicsV2;
