import KeyboardArrowLeftIcon from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRightIcon from '@mui/icons-material/KeyboardArrowRight';

import { useContext, useEffect, useState } from 'react';
import { Context } from '../../GlobalContext';
import axios from 'axios';
import { IconButton } from '@mui/material';

function ListLastUpdate() {
    const { servers } = useContext(Context);
    const [updatedComics, setUpdatedComics] = useState({});
    const fetchData = (page = 1) => {
        if (servers && servers.length > 0) {
            const server_id = servers.find((server) => server.priority === 1).id;
            axios
                .get(`http://localhost:8080/api/v1/comic/lasted-comic`, {
                    params: {
                        server_id,
                        page,
                    },
                })
                .then((response) => {
                    if (response.data.statusCode === 200) {
                        setUpdatedComics({
                            comics: response.data.data,
                            pagination: response.data.pagination,
                        });
                    }
                })
                .catch((err) => {
                    // thong bao loi
                    console.log(err);
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
        <div className="min-h-96 mx-auto mt-32" style={{ maxWidth: 1200 }}>
            <h2 className="text-3xl font-medium underline">Truyện mới cập nhật: </h2>

            <table className="divide-dashed divide-slate-400 w-full mt-4 text-lg text-gray-500 font-medium">
                <tbody className="divide-y">
                    {updatedComics.comics &&
                        updatedComics.comics.map((comic) => {
                            return (
                                <tr key={comic.tagId} className="divide-x divide-dashed divide-slate-400">
                                    <td className="p-2 lg:w-1/3 md:w-1/2">
                                        <KeyboardArrowRightIcon sx={{ fontSize: 28, marginBottom: 0.5 }} />
                                        {comic.title}
                                    </td>
                                    {comic.genres.length > 0 && (
                                        <td className="p-2 w-1/3">
                                            {comic.genres.map((genre) => genre.label).join(', ')}
                                        </td>
                                    )}
                                    {comic.newestChapter && (
                                        <td className="p-2 text-violet-500 lg:w-1/6 md:w-1/5">
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

export default ListLastUpdate;
