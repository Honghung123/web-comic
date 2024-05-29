import { useContext, useEffect, useState } from 'react';
import KeyboardArrowLeftIcon from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRightIcon from '@mui/icons-material/KeyboardArrowRight';
import { IconButton } from '@mui/material';
import axios from 'axios';
import { toast } from 'react-toastify';
import { Link } from 'react-router-dom';

import { Context } from '../../GlobalContext';

function ListLastUpdated() {
    const { servers } = useContext(Context);
    const [updatedComics, setUpdatedComics] = useState({});
    const fetchData = (page = 1) => {
        if (servers && servers.length > 0) {
            const server_id = servers[0].id;
            axios
                .get(`http://localhost:8080/api/v1/comic/lasted-comic`, {
                    params: {
                        server_id,
                        page,
                    },
                    headers: {
                        'list-crawlers': JSON.stringify(servers.map((server) => server.id)),
                    },
                })
                .then((response) => {
                    console.log('last update chapters: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        setUpdatedComics({
                            comics: responseData.data,
                            pagination: responseData.pagination,
                        });
                    } else {
                        console.log(responseData.message);
                        toast.error(responseData.message);
                    }
                })
                .catch((err) => {
                    // thong bao loi
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
    };
    useEffect(() => {
        fetchData();
    }, [servers]);

    const handleChangePage = (e) => {
        let page = updatedComics.pagination?.currentPage;
        if (!page) return;
        if (e.target.ariaLabel === 'next') {
            page = page + 1;
        } else {
            page = page - 1;
        }
        fetchData(page);
    };

    return (
        <div className="min-h-96 p-2 mx-auto mt-32" style={{ maxWidth: 1200 }}>
            <h2 className="text-3xl font-medium underline underline-offset-8">Truyện mới cập nhật: </h2>

            <table className="divide-dashed divide-slate-400 w-full mt-4 text-lg text-gray-500 font-medium">
                <tbody className="divide-y">
                    {updatedComics.comics &&
                        updatedComics.comics.map((comic) => {
                            return (
                                <tr key={comic.tagId} className="divide-x divide-dashed divide-slate-400">
                                    <td className="p-2 lg:w-1/3 md:w-1/2">
                                        <KeyboardArrowRightIcon sx={{ fontSize: 28, marginBottom: 0.5 }} />
                                        <Link
                                            className="hover:text-purple-500"
                                            to={`/info/${servers[0]?.id}/${comic.tagId}`}
                                        >
                                            {comic.title}
                                        </Link>
                                    </td>
                                    {comic.genres.length > 0 && (
                                        <td className="p-2 w-1/3">
                                            {comic.genres.map((genre, index) => (
                                                <>
                                                    <Link key={index} to={`/genre/${servers[0]?.id}/${genre.tag}`}>
                                                        <span className="hover:text-purple-500">{genre.label}</span>
                                                    </Link>
                                                    {index < comic.genres.length - 1 && <>, </>}
                                                </>
                                            ))}
                                        </td>
                                    )}
                                    {comic.newestChapter && (
                                        <td className="p-2 text-purple-500 lg:w-1/6 md:w-1/5">
                                            Chương {comic.newestChapter}
                                        </td>
                                    )}
                                    {comic.updatedTime && <td className="p-2">{comic.updatedTime}</td>}
                                </tr>
                            );
                        })}
                </tbody>
            </table>

            <div className="flex justify-end">
                <IconButton
                    color="secondary"
                    aria-label="previous"
                    disabled={updatedComics?.pagination?.currentPage === 1}
                    onClick={handleChangePage}
                >
                    <KeyboardArrowLeftIcon aria-label="previous" sx={{ fontSize: 32 }} />
                </IconButton>
                <IconButton
                    color="secondary"
                    aria-label="next"
                    disabled={updatedComics?.pagination?.currentPage === updatedComics?.pagination?.totalPages}
                    onClick={handleChangePage}
                >
                    <KeyboardArrowRightIcon aria-label="next" sx={{ fontSize: 32 }} />
                </IconButton>
            </div>
        </div>
    );
}

export default ListLastUpdated;
