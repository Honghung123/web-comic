import { useContext, useEffect, useState } from 'react';
import KeyboardArrowLeftIcon from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRightIcon from '@mui/icons-material/KeyboardArrowRight';
import { IconButton } from '@mui/material';
import axios from 'axios';
import { toast } from 'react-toastify';
import { Link } from 'react-router-dom';

import { Context } from '../GlobalContext';
import * as Utils from '../../utils';

function ListLastUpdated() {
    const { servers } = useContext(Context);
    const [updatedComics, setUpdatedComics] = useState({});

    const fetchData = (page = 1) => {
        if (servers && servers.length > 0) {
            const server_id = servers[0].id;
            axios
                .get(`${process.env.REACT_APP_API_URL}/comic/lasted-comic`, {
                    params: {
                        server_id,
                        page,
                    },
                    headers: {
                        list_crawlers: JSON.stringify(servers.map((server) => server.id)),
                    },
                })
                .then((response) => {
                    console.log('last update chapters: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        // convert datetime to string

                        setUpdatedComics({
                            comics: responseData.data,
                            pagination: responseData.pagination,
                        });
                    } else {
                        console.log(responseData.message);
                        toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
                    }
                })
                .catch((err) => {
                    // thong bao loi
                    console.log(err);
                    if (err.response?.status === 400) {
                        // back end update list servers
                        toast.error('Hệ thống đã cập nhật. Vui lòng tải lại trang!', { toastId: 400 });
                    } else {
                        toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
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
        <div className="min-h-96 p-2 mx-auto mt-16 max-w-[1200px]">
            <h2 className="text-3xl font-medium underline underline-offset-8">Truyện mới cập nhật: </h2>

            <table className="divide-dashed divide-slate-400 w-full mt-4 text-lg text-gray-500 font-medium">
                <tbody className="divide-y">
                    {updatedComics.comics &&
                        updatedComics.comics.map((comic) => {
                            return (
                                <tr key={comic.tagId} className="divide-x divide-dashed divide-slate-400">
                                    <td className="p-2 w-9/12 lg:w-1/2 xl:w-5/12">
                                        <div className="line-clamp-1">
                                            <KeyboardArrowRightIcon sx={{ fontSize: 28, marginBottom: 0.5 }} />
                                            <Link
                                                className="hover:text-purple-500"
                                                to={`/info/${servers[0]?.id}/${comic.tagId}`}
                                            >
                                                {comic.title}
                                            </Link>
                                        </div>
                                    </td>
                                    {comic.genres.length > 0 && (
                                        <td className="p-2 hidden lg:block">
                                            <div className="line-clamp-1 xl:w-72 w-56">
                                                {comic.genres.map((genre, index) => (
                                                    <span key={index} className="w-full">
                                                        <Link to={`/genre/${servers[0]?.id}/${genre.tag}`}>
                                                            <span className="hover:text-purple-500">{genre.label}</span>
                                                        </Link>
                                                        {index < comic.genres.length - 1 && <>, </>}
                                                    </span>
                                                ))}
                                            </div>
                                        </td>
                                    )}
                                    {comic.newestChapter && (
                                        <td className="p-2 w-40 text-purple-500">
                                            <div className="w-40">Chương {comic.newestChapter}</div>
                                        </td>
                                    )}
                                    {comic.updatedTime && (
                                        <td className="p-2 w-40 hidden lg:block">
                                            <div className="w-40">{Utils.getDiffTime(comic.updatedTime)}</div>
                                        </td>
                                    )}
                                </tr>
                            );
                        })}
                </tbody>
            </table>

            <div className="flex justify-end items-center">
                <IconButton
                    color="secondary"
                    aria-label="previous"
                    disabled={updatedComics?.pagination?.currentPage === 1}
                    onClick={handleChangePage}
                >
                    <KeyboardArrowLeftIcon aria-label="previous" sx={{ fontSize: 32 }} />
                </IconButton>
                <span className="text-gray-400">{updatedComics.pagination?.currentPage}</span>
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
