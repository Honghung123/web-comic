import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { Button } from '@mui/material';

import { Context } from '../../GlobalContext';

function ComicDetail({ tagId }) {
    const { servers } = useContext(Context);
    const [comicData, setComicData] = useState();
    const [showFullDescription, setShowFullDescription] = useState(false);
    const toggleDescription = () => {
        setShowFullDescription(!showFullDescription);
    };
    const truncate = (text, length) => {
        return text.length > length ? text.slice(0, length) : text;
    };

    useEffect(() => {
        if (servers && servers.length > 0) {
            const server_id = servers.find((server) => server.priority === 1).id;
            axios
                .get(`http://localhost:8080/api/v1/comic/reading/${tagId}`, {
                    params: { server_id },
                })
                .then((response) => {
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        console.log(responseData.data);
                        setComicData(responseData.data);
                    } else {
                        //thong bao loi
                        console.log(responseData.message);
                    }
                })
                .catch((err) => {
                    //thong bao loi
                    console.log(err);
                });
        }
    }, []);

    return (
        <div className="min-h-96 my-16 mx-auto relative" style={{ maxWidth: 1200 }}>
            {comicData && (
                <div className="flex flex-wrap">
                    <div className="md:w-1/4 sm:w-1/3 w-full overflow-hidden">
                        <img
                            className="w-full object-cover hover:transform hover:scale-110 transition-all duration-300 shadow-lg"
                            src={comicData.image}
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
                            <Button variant="contained" color="success" sx={{ borderRadius: 40 }}>
                                Đọc từ đầu
                            </Button>
                            <Button variant="contained" color="success" sx={{ borderRadius: 40 }}>
                                Tiếp tục
                            </Button>
                            <Button variant="contained" color="success" sx={{ borderRadius: 40 }}>
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
