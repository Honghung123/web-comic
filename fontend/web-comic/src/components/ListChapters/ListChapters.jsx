import { Divider, Pagination, Stack, PaginationItem } from '@mui/material';
import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Context } from '../../GlobalContext';

function ListChapters({ tagId }) {
    const { servers } = useContext(Context);
    const [chapters, setChapters] = useState();
    const [page, setPage] = useState(1);
    const [pagination, setPagination] = useState();

    const handleChangePage = (e, value) => {
        setPage(value);
    };

    useEffect(() => {
        if (servers && servers.length > 0) {
            const server_id = servers.find((server) => server.priority === 1).id;
            axios
                .get(`http://localhost:8080/api/v1/comic/reading/${tagId}/chapters`, {
                    params: {
                        server_id,
                        page,
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
                    }
                })
                .catch((err) => {
                    // thong bao loi
                    console.log(err);
                });
        }
    }, [page]);

    return (
        <div className="min-h-32 mt-16 mx-auto relative" style={{ maxWidth: 1200 }}>
            <div className="text-3xl font-semibold">Danh sách chương: </div>
            <Divider orientation="horizontal" className="h-4" />
            <ul>
                {chapters &&
                    chapters.map((chapter) => (
                        <Link key={chapter.chapterNo} className="block" to={`/reading/${tagId}/${chapter.chapterNo}`}>
                            Chương {chapter.chapterNo}: {chapter.title}
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
