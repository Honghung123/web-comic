import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { Button } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

import { Context } from '../../components/GlobalContext';
import * as Utils from '../../utils';
import SettingServer from '../SettingServer';

function ComicDetail({ tagId, serverId }) {
    const navigate = useNavigate();
    const { servers } = useContext(Context);
    const [comicData, setComicData] = useState();
    const [showFullDescription, setShowFullDescription] = useState(false);
    const [chapterBound, setChapterBound] = useState();
    const [serverIdState, setServerIdState] = useState(serverId);

    const toggleDescription = () => {
        setShowFullDescription(!showFullDescription);
    };
    const truncate = (text, length) => {
        return text.length > length ? text.slice(0, length) : text;
    };

    useEffect(() => {
        window.scrollTo(0, 0);
        if (serverId !== undefined) {
            axios
                .get(`${process.env.REACT_APP_API_URL}/comic/reading/${tagId}`, {
                    params: { server_id: serverIdState },
                    headers: {
                        'list-crawlers': JSON.stringify(servers.map((server) => server.id)),
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
                .get(`${process.env.REACT_APP_API_URL}/comic/reading/${tagId}/chapters`, {
                    params: {
                        server_id: serverId,
                        page: 1,
                    },
                    headers: {
                        'list-crawlers': JSON.stringify(servers.map((server) => server.id)),
                    },
                })
                .then((response) => {
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        console.log('chapter bound: ', responseData);
                        setChapterBound({ first: responseData.data[0] });
                        axios
                            .get(`${process.env.REACT_APP_API_URL}/comic/reading/${tagId}/chapters`, {
                                params: {
                                    server_id: serverId,
                                    page: responseData.pagination?.totalPages,
                                },
                                headers: {
                                    'list-crawlers': JSON.stringify(servers.map((server) => server.id)),
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
                            })
                            .catch((err) => {
                                // nothing to do
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

    // change comic's source
    useEffect(() => {
        if (comicData && servers && servers.length > 0) {
            const { id: server_id, name: server_name } = servers.find((server) => server.id === serverIdState);

            console.log('post body: ', {
                title: comicData.title,
                authorName: comicData.author?.name,
                comicTagId: comicData.tagId,
                chapterNumber: 1,
            });

            const fecthData = async () => {
                try {
                    const response = await axios.post(
                        `${process.env.REACT_APP_API_URL}/comic/reading/change-server-comic-info`,
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
                                'list-crawlers': JSON.stringify(servers.map((server) => server.id)),
                            },
                        },
                    );
                    console.log('change server: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        // toast.success('Data fetched successfully!');
                        navigate(`/info/${serverIdState}/${responseData.data.tagId}`);
                    } else {
                        // thong bao loi
                        console.log(responseData.message);
                        setTimeout(() => {
                            setServerIdState(serverId);
                        }, 700);
                        throw new Error(responseData.message);
                        // alert(responseData.message);
                    }
                } catch (err) {
                    // thong bao loi
                    // alert(err.message);
                    console.log(err);
                    setTimeout(() => {
                        setServerIdState(serverId);
                    }, 700);
                    throw err;
                }
            };

            if (serverId !== serverIdState) {
                toast.promise(fecthData(), {
                    pending: `Chuyển sang server ${server_name}`,
                    success: 'Chuyển server thành công',
                    error: `Không tìm thấy truyện trên ${server_name}`,
                });
            }
        }
    }, [serverIdState]);

    console.log('comic data: ', comicData);

    return (
        <div className="min-h-96 mb-16 mx-auto relative" style={{ maxWidth: 1200 }}>
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
                                className="text-center"
                                component={Link}
                                to={
                                    chapterBound && chapterBound.first
                                        ? `/reading/${serverId}/${tagId}/${chapterBound.first.chapterNo}`
                                        : `/info/${serverId}/${tagId}`
                                }
                                variant="contained"
                                color="success"
                                sx={{ borderRadius: 40, width: 148 }}
                            >
                                Đọc từ đầu
                            </Button>
                            <Button
                                className="text-center"
                                disabled={Utils.getLastReadingChapter(tagId, serverId) === undefined}
                                component={Link}
                                to={`/reading/${serverId}/${tagId}/${Utils.getLastReadingChapter(tagId, serverId)}`}
                                variant="contained"
                                color="success"
                                sx={{ borderRadius: 40 }}
                            >
                                Tiếp tục
                            </Button>
                            <Button
                                className="text-center"
                                component={Link}
                                to={
                                    chapterBound && chapterBound.last
                                        ? `/reading/${serverId}/${tagId}/${chapterBound.last.chapterNo}`
                                        : `/info/${serverId}/${tagId}`
                                }
                                variant="contained"
                                color="success"
                                sx={{ borderRadius: 40, width: 148 }}
                            >
                                Đọc mới nhất
                            </Button>
                        </div>
                    </div>
                    <div className="w-full mt-4">
                        <div className="text-xl font-semibold">
                            Thể loại:{' '}
                            {comicData.genres.length > 0 &&
                                comicData.genres.map((genre, index) => (
                                    <>
                                        <Link key={index} to={`/genre/${serverId}/${genre.tag}`}>
                                            <span className="hover:text-purple-500">{genre.label}</span>
                                        </Link>
                                        {index < comicData.genres.length - 1 && <>, </>}
                                    </>
                                ))}
                        </div>
                        <div className="text-xl font-semibold mt-4">
                            Tác giả:{' '}
                            <Link
                                to={`/author/${serverId}/${comicData.author?.authorId}/${comicData.tagId}`}
                                className="hover:text-purple-500"
                            >
                                {' ' + comicData.author?.name}
                            </Link>
                        </div>
                        <SettingServer serverId={serverIdState} setServerId={setServerIdState} />
                    </div>
                </div>
            )}
        </div>
    );
}

export default ComicDetail;
