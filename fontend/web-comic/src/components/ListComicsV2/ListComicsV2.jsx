import { useContext, useEffect, useState } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import axios from 'axios';
import { Pagination, Stack } from '@mui/material';

import { Context } from '../GlobalContext';
import Loading from '../Loading';
import * as Utils from '../../utils';

function ListComicsV2() {
    const location = useLocation();
    const { pathname } = location;
    const isAuthorPage = pathname.startsWith('/author');
    const seperateIdices = Utils.getIdicesOfCharacter(pathname, '/');
    const serverId = pathname.substring(seperateIdices[1] + 1, seperateIdices[2]);
    const authorId = isAuthorPage ? pathname.substring(seperateIdices[2] + 1, seperateIdices[3]) : '';
    const tagId = isAuthorPage ? pathname.substring(seperateIdices[3] + 1) : '';
    const genre = isAuthorPage ? '' : pathname.substring(seperateIdices[2] + 1);
    const { servers } = useContext(Context);
    const [comicsData, setComicsData] = useState({});
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setPage(1);
    }, [pathname]);

    useEffect(() => {
        window.scrollTo(0, 0);
        setComicsData({ ...comicsData, comics: undefined });
        if (serverId) {
            setLoading(true);
            let requestUrl = isAuthorPage
                ? `${process.env.REACT_APP_API_URL}/comic/author/${authorId}`
                : `${process.env.REACT_APP_API_URL}/comic/search`;

            axios
                .get(requestUrl, {
                    params: {
                        server_id: serverId,
                        page,
                        genre,
                        tagId,
                    },
                    headers: {
                        list_crawlers: JSON.stringify(servers.map((server) => server.id)),
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
                        toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
                    }
                    setLoading(false);
                })
                .catch((err) => {
                    //thong bao loi
                    console.log(err);
                    if (err.response?.status === 400) {
                        // back end update list servers
                        toast.error('Hệ thống đã cập nhật. Vui lòng tải lại trang!', { toastId: 400 });
                    } else {
                        toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
                    }
                    setLoading(false);
                });
        }
    }, [page, pathname]);

    return (
        <div className="p-2 mx-auto relative max-w-[1000px] min-h-[480px]">
            <Loading loading={loading} />
            {comicsData.comics && (
                <>
                    <h2 className="text-3xl font-semibold underline underline-offset-8">
                        {comicsData.comics.length > 0 &&
                            (isAuthorPage
                                ? `Tác giả ${comicsData.comics[0].author.name || authorId}`
                                : `Thể loại ${
                                      JSON.parse(localStorage.getItem('genres'))?.find(
                                          (genreItem) => genreItem.tag === genre,
                                      ).label
                                  }`)}
                    </h2>
                    <div className="divide-y">
                        {comicsData.comics.map((comic) => {
                            return (
                                <div className="my-2 py-2 flex items-center justify-between">
                                    <div className="flex items-center">
                                        <div className="w-32 md:w-52 h-32 overflow-hidden">
                                            <img
                                                className="w-full h-full object-cover hover:transform hover:scale-110 transition-all duration-300"
                                                src={comic.image}
                                                onError={(e) => {
                                                    e.target.src = comic.alternateImage;
                                                }}
                                                alt={comic.tagId}
                                            />
                                        </div>
                                        <div className="px-4 flex-1 max-w-[580px]">
                                            <Link to={`/info/${serverId}/${comic.tagId}`}>
                                                <h3 className="text-xl font-semibold hover:text-purple-500 line-clamp-2">
                                                    {comic.title}
                                                </h3>
                                            </Link>
                                            <Link to={`/author/${serverId}/${comic.author.authorId}/${comic.tagId}`}>
                                                <div className="italic py-2 hover:text-purple-500">
                                                    {comic.author.name}
                                                </div>
                                            </Link>
                                            {comic.genres.length > 0 && (
                                                <div>
                                                    Thể loại:{' '}
                                                    {comic.genres.map((genre, index) => (
                                                        <>
                                                            <Link key={index} to={`/genre/${serverId}/${genre.tag}`}>
                                                                <span className="hover:text-purple-500">
                                                                    {genre.label}
                                                                </span>
                                                            </Link>
                                                            {index < comic.genres.length - 1 && <>, </>}
                                                        </>
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
