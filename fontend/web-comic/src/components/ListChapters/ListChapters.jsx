import { Divider, Pagination, Stack } from '@mui/material';
import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';

import { Context } from '../GlobalContext';
import Loading from '../Loading';
import * as Utils from '../../utils';

function ListChapters({ tagId, serverId, headerSize = 'text-3xl' }) {
    const { servers } = useContext(Context);
    const [chapters, setChapters] = useState();
    const [page, setPage] = useState(1);
    const [pagination, setPagination] = useState();
    const [loading, setLoading] = useState(false);

    const handleChangePage = (e, value) => {
        setPage(value);
    };

    useEffect(() => {
        if (serverId !== undefined) {
            setLoading(true);
            axios
                .get(`${process.env.REACT_APP_API_URL}/comic/reading/${tagId}/chapters`, {
                    params: {
                        server_id: serverId,
                        page,
                    },
                    headers: {
                        list_crawlers: JSON.stringify(servers.map((server) => server.id)),
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
                        toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
                    }
                    setLoading(false);
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
                    setLoading(false);
                });
        }
    }, [page, tagId]);

    return (
        <div className="min-h-64 mx-auto relative max-w-[1200px]">
            <Loading loading={loading} />
            <div className={`${headerSize} font-semibold`}>Danh sách chương: </div>
            <Divider orientation="horizontal" className={`${headerSize === 'text-xl' ? 'h-2' : 'h-4'}`} />
            <ul>
                {chapters &&
                    chapters.map((chapter) => (
                        <Link
                            key={chapter.chapterNo}
                            className="block"
                            to={`/reading/${serverId}/${tagId}/${chapter.chapterNo}`}
                        >
                            <div
                                className={`hover:bg-purple-100/50 rounded ${
                                    Utils.isRead(chapter.chapterNo, tagId, serverId)
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
