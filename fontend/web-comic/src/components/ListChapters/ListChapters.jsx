import { Divider, Pagination, Stack, PaginationItem } from '@mui/material';
import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';

import { Context } from '../../GlobalContext';
import Loading from '../Loading';
import * as Utils from '../../utils';

function ListChapters({ tagId, headerSize = 'text-3xl' }) {
    const { servers } = useContext(Context);
    const [chapters, setChapters] = useState();
    const [page, setPage] = useState(1);
    const [pagination, setPagination] = useState();
    const [loading, setLoading] = useState(false);

    const handleChangePage = (e, value) => {
        setPage(value);
    };

    let server_id;
    if (servers && servers.length > 0) {
        server_id = servers.find((server) => server.priority === 1).id;
    }

    useEffect(() => {
        if (server_id !== undefined) {
            setLoading(true);
            axios
                .get(`http://localhost:8080/api/v1/comic/reading/${tagId}/chapters`, {
                    params: {
                        server_id,
                        page,
                    },
                    headers: {
                        'crawler-size': servers.length,
                    },
                })
                .then((response) => {
                    const responseData = response.data;
                    console.log(responseData);
                    if (responseData.statusCode === 200) {
                        setChapters(responseData.data);
                        setPagination(responseData.pagination);
                    } else {
                        // thong bao loi
                        console.log(responseData.message);
                        toast.error(responseData.message);
                    }
                    setLoading(false);
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
                    setLoading(false);
                });
        }
    }, [page, tagId]);

    return (
        <div className="min-h-32 mx-auto relative" style={{ maxWidth: 1200 }}>
            <Loading loading={loading} />
            <div className={`${headerSize} font-semibold`}>Danh sách chương: </div>
            <Divider orientation="horizontal" className={`${headerSize === 'text-xl' ? 'h-2' : 'h-4'}`} />
            <ul>
                {chapters &&
                    chapters.map((chapter) => (
                        <Link key={chapter.chapterNo} className="block" to={`/reading/${tagId}/${chapter.chapterNo}`}>
                            <div
                                className={`hover:bg-purple-100/50 rounded ${
                                    Utils.isRead(chapter.chapterNo, tagId, server_id)
                                        ? 'text-gray-300'
                                        : 'text-purple-500'
                                }`}
                            >
                                Chương {chapter.chapterNumber}: {chapter.title}
                            </div>
                        </Link>
                    ))}
            </ul>
            <Stack spacing={2} className="mt-8" direction="row" justifyContent="center">
                <Pagination
                    showFirstButton
                    showLastButton
                    variant="outlined"
                    color="secondary"
                    page={page}
                    count={(pagination && pagination.totalPages) || 1}
                    onChange={handleChangePage}
                />
            </Stack>
        </div>
    );
}

export default ListChapters;
