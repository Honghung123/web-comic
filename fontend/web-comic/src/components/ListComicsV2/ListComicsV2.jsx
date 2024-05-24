import { useContext, useEffect, useState } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import axios from 'axios';
import { Pagination, PaginationItem, Stack } from '@mui/material';

import { Context } from '../../GlobalContext';
function ListComicsV2() {
    const location = useLocation();
    const { pathname } = location;
    const isAuthorPage = pathname.startsWith('/author');
    const authorId = isAuthorPage ? pathname.substring(pathname.lastIndexOf('/') + 1) : '';
    const genre = isAuthorPage ? '' : pathname.substring(pathname.lastIndexOf('/') + 1);
    const { servers } = useContext(Context);
    const [comicsData, setComicsData] = useState({});
    const [page, setPage] = useState(1);

    useEffect(() => {
        setPage(1);
    }, [pathname]);

    useEffect(() => {
        window.scrollTo(0, 0);
        setComicsData({ ...comicsData, comics: undefined });
        if (servers && servers.length > 0) {
            const server_id = servers.find((server) => server.priority === 1).id;

            let requestUrl = isAuthorPage
                ? `http://localhost:8080/api/v1/comic/author/${authorId}`
                : 'http://localhost:8080/api/v1/comic/search';

            axios
                .get(requestUrl, {
                    params: {
                        server_id,
                        page,
                        genre,
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
    }, [page, pathname]);

    return (
        <div className="min-h-96 p-2 mx-auto relative" style={{ maxWidth: 1000 }}>
            {comicsData.comics && (
                <>
                    <h2 className="text-3xl font-semibold underline underline-offset-8">
                        {comicsData.comics.length > 0 &&
                            (isAuthorPage ? `Tác giả ${comicsData.comics[0].author.name}` : `Thể loại ${genre}`)}
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
                                                alt={comic.tagId}
                                            />
                                        </div>
                                        <div className="px-4">
                                            <Link to={`/info/${comic.tagId}`}>
                                                <h3
                                                    className="text-xl font-semibold hover:text-purple-500"
                                                    style={{ maxWidth: 580 }}
                                                >
                                                    {comic.title}
                                                </h3>
                                            </Link>
                                            <Link to={`/author/${comic.author.authorId}`}>
                                                <div className="italic py-2 hover:text-purple-500">
                                                    {comic.author.name}
                                                </div>
                                            </Link>
                                            {comic.genres.length > 0 && (
                                                <div>
                                                    The loai:{' '}
                                                    {comic.genres.map((genre) => (
                                                        <Link to={`/genre/${genre.tag}`}>
                                                            <span className="hover:text-purple-500">{genre.label}</span>
                                                        </Link>
                                                    ))}
                                                </div>
                                            )}
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

            {!isAuthorPage && (
                <Stack spacing={2} className="mt-8" direction="row" justifyContent="center">
                    <Pagination
                        showFirstButton
                        showLastButton
                        variant="outlined"
                        color="secondary"
                        page={page}
                        count={(comicsData.pagination && comicsData.pagination.totalPages) || 1}
                        onChange={(event, value) => {
                            setPage(value);
                        }}
                    />
                </Stack>
            )}
        </div>
    );
}

export default ListComicsV2;
