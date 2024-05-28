import axios from 'axios';
import { useContext, useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Pagination, PaginationItem, Stack } from '@mui/material';
import { toast } from 'react-toastify';

import { Context } from '../../GlobalContext';
import ComicItem from '../ComicItem';
import Loading from '../Loading';

function ListComics() {
    const { servers } = useContext(Context);

    const [comicsData, setComicsData] = useState({});

    const [searchParams] = useSearchParams();
    const page = parseInt(searchParams.get('page')) || 1;
    const genre = searchParams.get('genre') || '';
    const keyword = searchParams.get('keyword') || '';

    const [loading, setLoading] = useState(false);

    useEffect(() => {
        window.scrollTo(0, 0);
        setComicsData({ ...comicsData, comics: undefined, others: undefined });
        if (servers && servers.length > 0) {
            setLoading(true);
            const server_id = servers.find((server) => server.priority === 1).id;
            console.log(JSON.stringify(servers.map((server) => server.id)));
            axios
                .get(`http://localhost:8080/api/v1/comic/search`, {
                    params: {
                        server_id,
                        page,
                        genre,
                        keyword,
                    },
                    headers: {
                        'list-crawlers': JSON.stringify(servers.map((server) => server.id)),
                    },
                })
                .then((response) => {
                    const responseData = response.data;
                    console.log('list-comic: ', responseData);
                    if (responseData.statusCode === 200) {
                        setComicsData({
                            comics: responseData.data,
                            pagination: responseData.pagination,
                            others: responseData.others || [],
                        });
                    } else {
                        // thong bao loi
                        console.log(responseData.message);
                        toast.error(responseData.message);
                    }
                    setLoading(false);
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
                    setLoading(false);
                });
        }
    }, [servers, genre, keyword, page]);

    // console.log('commic data: ', comicsData);
    const getSearchStr = (keyword, genre, page) => {
        let searchStr =
            `?${keyword === '' ? '' : `keyword=${keyword}`}` +
            `${genre === '' ? '' : `&genre=${genre}`}` +
            `${page === 1 ? '' : `&page=${page}`}`;
        if (searchStr.length > 0 && searchStr[0] === '&') {
            searchStr = searchStr.substring(1);
        }
        return searchStr;
    };

    let headerText = 'Danh sách truyện đề cử:';
    if (keyword !== '' && genre === '') {
        headerText = `Tìm kiếm cho: "${keyword}"`;
    } else if (genre !== '') {
        const listGenres = JSON.parse(localStorage.getItem('genres'));
        const genreLabel = listGenres?.find((genreItem) => genreItem.tag === genre).label;
        if (keyword === '') {
            headerText = `Tìm kiếm theo thể loại: "${genreLabel}"`;
        } else {
            headerText = `Tìm kiếm cho: "${keyword}". Thể loại: ${genreLabel}.`;
        }
    }

    return (
        <div className="min-h-96 p-2 mt-8 mx-auto relative" style={{ maxWidth: 1200 }}>
            <Loading loading={loading} />
            <h2 className="text-3xl pt-2 font-semibold underline underline-offset-8">{headerText}</h2>
            {keyword !== '' && genre === '' && comicsData.others && comicsData.others.length > 0 && (
                <div>
                    <h3 className="text-xl font-semibold underline underline-offset-4 mt-2">Danh sách tác giả:</h3>
                    <div className="my-2">
                        {comicsData.others.map((author, index) => (
                            <>
                                <Link key={index} to={`/author/${author.authorId}`}>
                                    <span className="hover:text-purple-500 pl-1 italic text-xl">{author.name}</span>
                                </Link>
                                {index < comicsData.others.length - 1 && <>, </>}
                            </>
                        ))}
                    </div>
                    <h3 className="text-xl font-semibold underline underline-offset-4 mt-4">Danh sách truyện:</h3>
                </div>
            )}
            <div className="flex flex-wrap min-h-full" style={{ marginLeft: '-1rem', marginRight: '-1rem' }}>
                {comicsData.comics &&
                    comicsData.comics.map((comic) => (
                        <div key={comic.tagId} className="lg:w-1/6 md:w-1/4 sm:w-1/3 w-1/2 px-4 mt-8">
                            <ComicItem comic={comic} />
                        </div>
                    ))}
            </div>

            <Stack spacing={2} className="mt-8" direction="row" justifyContent="center">
                <Pagination
                    showFirstButton
                    showLastButton
                    variant="outlined"
                    color="secondary"
                    page={page}
                    count={(comicsData.pagination && comicsData.pagination.totalPages) || 1}
                    renderItem={(item) => {
                        return (
                            <PaginationItem component={Link} to={getSearchStr(keyword, genre, item.page)} {...item} />
                        );
                    }}
                />
            </Stack>
        </div>
    );
}

export default ListComics;
