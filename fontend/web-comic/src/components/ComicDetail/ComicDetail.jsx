import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { Button } from '@mui/material';

import { Context } from '../../GlobalContext';
import * as Utils from '../../utils';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

function ComicDetail({ tagId }) {
    const navigate = useNavigate();
    const { servers } = useContext(Context);
    const [comicData, setComicData] = useState();
    const [showFullDescription, setShowFullDescription] = useState(false);
    const [chapterBound, setChapterBound] = useState();
    const toggleDescription = () => {
        setShowFullDescription(!showFullDescription);
    };
    const truncate = (text, length) => {
        return text.length > length ? text.slice(0, length) : text;
    };

    let server_id;
    if (servers && servers.length > 0) {
        server_id = servers.find((server) => server.priority === 1).id;
    }

    useEffect(() => {
        window.scrollTo(0, 0);
        if (server_id !== undefined) {
            axios
                .get(`http://localhost:8080/api/v1/comic/reading/${tagId}`, {
                    params: { server_id },
                    headers: {
                        'crawler-size': servers.length,
                    },
                })
                .then((response) => {
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        console.log(responseData.data);
                        setComicData(responseData.data);
                    } else {
                        //thong bao loi
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

            axios
                .get(`http://localhost:8080/api/v1/comic/reading/${tagId}/chapters`, {
                    params: {
                        server_id,
                        page: 1,
                    },
                    headers: {
                        'crawler-size': servers.length,
                    },
                })
                .then((response) => {
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        console.log('chapter bound: ', responseData);
                        setChapterBound({ first: responseData.data[0] });
                        axios
                            .get(`http://localhost:8080/api/v1/comic/reading/${tagId}/chapters`, {
                                params: {
                                    server_id,
                                    page: responseData.pagination?.totalPages,
                                },
                                headers: {
                                    'crawler-size': servers.length,
                                },
                            })
                            .then((response) => {
                                if (response.data.statusCode === 200) {
                                    setChapterBound((prev) => {
                                        return {
                                            ...prev,
                                            last: response.data.data[response.data.data.length - 1],
                                        };
                                    });
                                }
                            });
                    } else {
                        // khong can thong bao loi
                        console.log(responseData.message);
                    }
                })
                .catch((err) => {
                    // khong can thong bao loi
                    console.log(err);
                });
        }
    }, [tagId]);

    useEffect(() => {
        if (comicData && servers && servers.length > 0) {
            const { id: server_id, name: server_name } = servers.find((server) => server.priority === 1);

            console.log('post body: ', {
                title: comicData.title,
                authorName: comicData.author?.name,
                comicTagId: comicData.tagId,
                chapterNumber: 1,
            });

            const fecthData = async () => {
                try {
                    const response = await axios.post(
                        'http://localhost:8080/api/v1/comic/reading/change-server-comic-info',
                        {
                            title: comicData.title,
                            authorName: comicData.author?.name,
                            comicTagId: comicData.tagId,
                            chapterNumber: 1,
                        },
                        {
                            params: {
                                server_id,
                            },
                            headers: {
                                'crawler-size': servers.length,
                            },
                        },
                    );
                    console.log('change server: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        // toast.success('Data fetched successfully!');
                        navigate(`/info/${responseData.data.tagId}`);
                    } else {
                        // thong bao loi
                        console.log(responseData.message);
                        throw new Error(responseData.message);
                        // alert(responseData.message);
                    }
                } catch (err) {
                    // thong bao loi
                    // alert(err.message);
                    console.log(err);
                    throw err;
                }
            };

            toast.promise(fecthData(), {
                pending: `Chuyển sang server ${server_name}`,
                success: 'Chuyển server thành công',
                error: `Không tìm thấy truyện trên ${server_name}`,
            });
        }
    }, [servers]);

    console.log('comic data: ', comicData);

    return (
        <div className="min-h-96 my-16 mx-auto relative" style={{ maxWidth: 1200 }}>
            {comicData && (
                <div className="flex flex-wrap">
                    <div className="md:w-1/4 sm:w-1/3 w-full overflow-hidden">
                        <img
                            className="w-full object-cover hover:transform hover:scale-110 transition-all duration-300 shadow-lg"
                            src={comicData.image}
                            onError={(e) => (e.target.src = comicData.alternateImage)}
                            alt={comicData.tagId}
                        />
                    </div>
                    <div className="md:w-3/4 sm:w-2/3 w-full sm:pl-8">
                        <div className="text-3xl font-semibold">{comicData.title}</div>
                        <div className="text-xl font-semibold mt-2">Mô tả:</div>
                        <span
                            dangerouslySetInnerHTML={{
                                __html: showFullDescription
                                    ? comicData.description
                                    : truncate(comicData.description, 750),
                            }}
                        ></span>
                        {!showFullDescription && (
                            <div className="text-stone-400 hover:cursor-pointer" onClick={toggleDescription}>
                                ...Xem thêm
                            </div>
                        )}
                        {showFullDescription && (
                            <div className="text-stone-400 hover:cursor-pointer" onClick={toggleDescription}>
                                Ẩn bớt
                            </div>
                        )}
                        <div className="flex gap-2 mt-8">
                            <Button
                                component={Link}
                                to={
                                    chapterBound && chapterBound.first
                                        ? `/reading/${tagId}/${chapterBound.first.chapterNo}`
                                        : `/info/${tagId}`
                                }
                                variant="contained"
                                color="success"
                                sx={{ borderRadius: 40 }}
                            >
                                Đọc từ đầu
                            </Button>
                            <Button
                                disabled={Utils.getLastReadingChapter(tagId, server_id) === undefined}
                                component={Link}
                                to={`/reading/${tagId}/${Utils.getLastReadingChapter(tagId, server_id)}`}
                                variant="contained"
                                color="success"
                                sx={{ borderRadius: 40 }}
                            >
                                Tiếp tục
                            </Button>
                            <Button
                                component={Link}
                                to={
                                    chapterBound && chapterBound.last
                                        ? `/reading/${tagId}/${chapterBound.last.chapterNo}`
                                        : `/info/${tagId}`
                                }
                                variant="contained"
                                color="success"
                                sx={{ borderRadius: 40 }}
                            >
                                Đọc mới nhất
                            </Button>
                        </div>
                    </div>
                    <div className="w-full h-32 mt-4">
                        <div className="text-xl font-semibold">
                            Thể loại:{' '}
                            {comicData.genres.length > 0 && comicData.genres.map((genre) => genre.label).join(', ')}
                        </div>
                        <div className="text-xl font-semibold mt-2">Tác giả: {comicData.author?.name}</div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ComicDetail;
