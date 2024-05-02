import axios from 'axios';
import { useContext, useEffect, useRef, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Pagination, PaginationItem, Stack } from '@mui/material';

import { Context } from '../../GlobalContext';
import ComicItem from '../ComicItem';
import Loading from '../Loading';

function ListComics() {
    const { servers, genre, keyword } = useContext(Context);

    const [comicsData, setComicsData] = useState({});

    const [searchParams, setSearchParams] = useSearchParams();
    console.log('search params: ', searchParams);
    const page = parseInt(searchParams.get('page')) || 1;

    useEffect(() => {
        if (servers && servers.length > 0) {
            console.log('use effce');
            const server_id = servers.find((server) => server.priority === 1).id;
            axios
                .get(`http://localhost:8080/api/v1/comic/search`, {
                    params: {
                        server_id,
                        page,
                        genre,
                        keyword,
                    },
                })
                .then((response) => {
                    if (response.data.statusCode == 200) {
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
    }, [servers, genre, keyword, page]);

    console.log('commic data: ', comicsData);
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

    return (
        <div className="min-h-screen mt-8 mx-auto relative" style={{ maxWidth: 1200 }}>
            <h2 className="text-3xl font-semibold underline">Tìm kiếm cho: {keyword}</h2>

            <div className="flex flex-wrap min-h-full" style={{ marginLeft: '-1rem', marginRight: '-1rem' }}>
                {comicsData.comics &&
                    comicsData.comics.map((comic) => (
                        <div className="lg:w-1/6 md:w-1/4 sm:w-1/3 w-1/2 px-4 mt-8">
                            <ComicItem key={comic.tagId} comic={comic} />
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
                    count={(comicsData.pagination && comicsData.pagination.totalPages) || 10}
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
