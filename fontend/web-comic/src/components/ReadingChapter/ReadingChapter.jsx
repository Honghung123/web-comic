import { Button } from '@mui/material';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import MenuRoundedIcon from '@mui/icons-material/MenuRounded';
import SettingsRoundedIcon from '@mui/icons-material/SettingsRounded';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import CheckRoundedIcon from '@mui/icons-material/CheckRounded';
import NavigateNextRoundedIcon from '@mui/icons-material/NavigateNextRounded';
import NavigateBeforeRoundedIcon from '@mui/icons-material/NavigateBeforeRounded';
import Divider from '@mui/material/Divider';
import Modal from '@mui/material/Modal';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import { useContext, useEffect, useRef, useState } from 'react';
import axios from 'axios';

import { Context } from '../../GlobalContext';
import ListChapters from '../ListChapters/ListChapters';
import Loading from '../Loading';
import * as Utils from '../../utils';
import DownloadModal from './DownloadModal';
import { fontFamilies, bgColors, lineHeights } from './constants';

function ReadingChapter() {
    const location = useLocation();
    const { pathname } = location;
    console.log(pathname);
    const navigate = useNavigate();
    const tempStr = pathname.substring(pathname.indexOf('/', 1) + 1);
    const tagId = tempStr.substring(0, tempStr.indexOf('/'));
    const chapter = tempStr.substring(tempStr.indexOf('/') + 1);
    const { servers } = useContext(Context);

    // state for modal
    const [openSetting, setOpenSetting] = useState(false);
    const [openListChapters, setOpenListChapters] = useState(false);
    const [openDownload, setOpenDownload] = useState(false);
    const [modalPosition, setModalPosition] = useState({ x: 0, y: 0 });

    // state for custom
    const [bgColor, setBgColor] = useState(localStorage.getItem('bgColor') || 'bg-green-100');
    const [fontFamily, setFontFamily] = useState(localStorage.getItem('fontFamily') || fontFamilies[0].value);
    const [lineHeight, setLineHeight] = useState(localStorage.getItem('lineHeight') || '150%');
    const [fontSize, setFontSize] = useState(Number(localStorage.getItem('fontSize') || '20'));

    const [chapterData, setChapterData] = useState();
    const [loading, setLoading] = useState(false);

    const contentRef = useRef();

    // fetch data
    useEffect(() => {
        if (servers && servers.length > 0) {
            setLoading(true);
            if (chapterData) {
                setChapterData({ ...chapterData, data: {} });
            }
            const server_id = servers.find((server) => server.priority === 1).id;

            axios
                .get(`http://localhost:8080/api/v1/comic/reading/${tagId}/chapters/${chapter}`, {
                    params: {
                        server_id,
                    },
                })
                .then((response) => {
                    console.log('chapter response: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        // save chapter into history
                        Utils.addChapter(chapter, tagId, server_id);
                        setChapterData({
                            data: responseData.data,
                            pagination: responseData.pagination,
                        });
                    } else {
                        // thong bao loi
                        console.log(responseData.message);
                    }
                    setLoading(false);
                })
                .catch((err) => {
                    // thong bao loi
                    console.log(err);
                    setLoading(false);
                });
        }
    }, [pathname]);

    console.log('chapterdata: ', chapterData);
    // change server when reading
    useEffect(() => {
        if (chapterData?.data && servers && servers.length > 0) {
            const server_id = servers.find((server) => server.priority === 1).id;
            console.log('post body: ', {
                title: chapterData.data.title,
                authorName: chapterData.data.author?.name,
                comicTagId: chapterData.data.comicTagId,
                chapterNumber: chapterData.data.chapterNo || 1,
            });
            setLoading(true);
            axios
                .post(
                    'http://localhost:8080/api/v1/comic/reading/change-server-chapter-content',
                    {
                        title: chapterData.data.title,
                        authorName: chapterData.data.author?.name,
                        comicTagId: chapterData.data.comicTagId,
                        chapterNumber: chapterData.data.chapterNumber,
                    },
                    {
                        params: {
                            server_id,
                        },
                    },
                )
                .then((response) => {
                    console.log('change server: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        navigate(`/reading/${responseData.data.comicTagId}/${responseData.pagination.currentPage}`);
                    } else {
                        // thong bao loi
                        console.log(responseData.message);
                        setLoading(false);
                        alert(responseData.message);
                    }
                })
                .catch((err) => {
                    // thong bao loi
                    alert(err.message);
                    setLoading(false);
                    console.log(err);
                });
        }
    }, [servers]);

    return (
        <>
            {chapterData && (
                <div className="min-h-96 mt-16 mx-auto" style={{ maxWidth: 1200 }}>
                    <h1 className="text-3xl text-center font-semibold">{chapterData.data.chapterTitle}</h1>
                    <div className="flex justify-center gap-4 mt-4">
                        <Button
                            disabled={!chapterData.pagination.link.prevPage}
                            component={Link}
                            to={`/reading/${tagId}/${chapterData.pagination.link.prevPage}`}
                            variant="contained"
                            color="success"
                            sx={{ width: 180, textAlign: 'center', padding: '8px 0' }}
                        >
                            <NavigateBeforeRoundedIcon />
                            Chương trước
                        </Button>

                        <Button
                            disabled={!chapterData.pagination.link.nextPage}
                            component={Link}
                            to={
                                chapterData.pagination.link.nextPage
                                    ? `/reading/${tagId}/${chapterData.pagination.link.nextPage}`
                                    : ''
                            }
                            variant="contained"
                            color="success"
                            sx={{ width: 180, textAlign: 'center', padding: '8px 0' }}
                        >
                            Chương sau
                            <NavigateNextRoundedIcon />
                        </Button>
                    </div>

                    {/* content + setting properties */}
                    <div className="w-full mx-auto px-16 mt-8 relative">
                        <Loading loading={loading} />
                        <div
                            ref={contentRef}
                            className={`w-full min-h-72 ${bgColor} sm:p-4 p-2`}
                            style={{ fontSize, lineHeight, fontFamily }}
                            dangerouslySetInnerHTML={{
                                __html: chapterData.data.content,
                            }}
                        ></div>
                        <div className={`${bgColor} absolute z-50 top-0 left-0 divide-black rounded`}>
                            <div
                                onClick={(e) => {
                                    const rect = contentRef.current.getBoundingClientRect();
                                    setOpenListChapters(true);
                                    setModalPosition({ x: rect.left - 4, y: rect.top - 4 });
                                }}
                                className={`flex justify-center items-center cursor-pointer ${
                                    openListChapters ? 'text-purple-500' : ''
                                }`}
                                style={{ width: 50, height: 50 }}
                            >
                                <MenuRoundedIcon />
                            </div>
                            <Divider orientation="horizontal" variant="middle" />
                            <div
                                onClick={(e) => {
                                    let rect = contentRef.current.getBoundingClientRect();
                                    setOpenSetting(true);
                                    setModalPosition({ x: rect.left - 4, y: rect.top - 4 });
                                }}
                                className={`flex justify-center items-center cursor-pointer ${
                                    openSetting ? 'text-purple-500' : ''
                                }`}
                                style={{ width: 50, height: 50 }}
                            >
                                <SettingsRoundedIcon />
                            </div>
                            <Divider orientation="horizontal" variant="middle" />
                            <div
                                onClick={(e) => {
                                    let rect = contentRef.current.getBoundingClientRect();
                                    setOpenDownload(true);
                                    setModalPosition({ x: rect.left - 4, y: rect.top - 4 });
                                }}
                                className={`flex justify-center items-center cursor-pointer ${
                                    openDownload ? 'text-purple-500' : ''
                                }`}
                                style={{ width: 50, height: 50 }}
                            >
                                <FileDownloadIcon />
                            </div>
                        </div>
                        <Modal
                            open={openListChapters}
                            onClose={() => {
                                setOpenListChapters(false);
                            }}
                            aria-labelledby="modal-modal-title"
                            aria-describedby="modal-modal-description"
                            BackdropProps={{
                                sx: { backgroundColor: 'transparent' },
                            }}
                            sx={{ top: modalPosition.y, left: modalPosition.x }}
                        >
                            <div className="w-96 h-64 overflow-auto bg-white rounded p-4 shadow">
                                <ListChapters headerSize="text-xl" tagId={tagId} />
                            </div>
                        </Modal>

                        <Modal
                            open={openSetting}
                            onClose={() => {
                                setOpenSetting(false);
                            }}
                            aria-labelledby="modal-modal-title"
                            aria-describedby="modal-modal-description"
                            BackdropProps={{
                                sx: { backgroundColor: 'transparent' },
                            }}
                            sx={{ top: modalPosition.y, left: modalPosition.x }}
                        >
                            <div
                                className="bg-white rounded p-4"
                                style={{ boxShadow: '0 0 8px rgba(0, 0, 0, 0.6)', width: 360 }}
                            >
                                <div className="text-xl font-semibold">Tùy chỉnh:</div>
                                <Divider orientation="horizontal" className="h-2" />
                                <div className="flex justify-between mt-4">
                                    <div className="">Theme</div>
                                    <div className="flex gap-4">
                                        {bgColors.map((color) => {
                                            return (
                                                <div
                                                    onClick={(e) => {
                                                        const tagName = e.target.tagName.toUpperCase();
                                                        let id;
                                                        if (tagName === 'DIV') id = e.target.id;
                                                        else if (tagName === 'SVG') id = e.target.parentNode.id;
                                                        else id = e.target.parentNode.parentNode.id;
                                                        if (id !== bgColor) {
                                                            localStorage.setItem('bgColor', id);
                                                            setBgColor(id);
                                                        }
                                                    }}
                                                    key={color}
                                                    id={color}
                                                    className={`rounded-full border text-purple-500 ${color} ${
                                                        bgColor === color ? 'border-purple-500' : ''
                                                    }  h-8 w-8 cursor-pointer text-center`}
                                                >
                                                    {bgColor === color && <CheckRoundedIcon />}
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>

                                <div className="flex justify-between gap-8 mt-4 items-center">
                                    <div className="">Font chu</div>
                                    <Select
                                        onChange={(e) => {
                                            localStorage.setItem('fontFamily', e.target.value);
                                            setFontFamily(e.target.value);
                                        }}
                                        sx={{ flex: 1 }}
                                        value={fontFamily}
                                        className="bg-white"
                                    >
                                        {fontFamilies.map((font) => (
                                            <MenuItem value={font.value} key={font.value}>
                                                {font.title}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </div>

                                <div className="flex justify-between gap-8 mt-4 items-center">
                                    <div className="">Cach dong</div>
                                    <Select
                                        onChange={(e) => {
                                            localStorage.setItem('lineHeight', e.target.value);
                                            setLineHeight(e.target.value);
                                        }}
                                        sx={{ flex: 1 }}
                                        value={lineHeight}
                                        className="bg-white"
                                    >
                                        {lineHeights.map((lh) => (
                                            <MenuItem value={lh} key={lh}>
                                                {lh}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </div>

                                <div className="flex justify-between gap-8 mt-4 items-center">
                                    <div className="">Co chu</div>
                                    <div className="flex grow pl-3">
                                        <Button
                                            onClick={(e) => {
                                                setFontSize((prev) => {
                                                    if (prev >= 14) {
                                                        localStorage.setItem('fontSize', prev - 2);
                                                        return prev - 2;
                                                    }
                                                    return prev;
                                                });
                                            }}
                                            variant="outlined"
                                            color="primary"
                                            sx={{ borderRadius: '20px 0 0 20px' }}
                                        >
                                            -
                                        </Button>
                                        <div
                                            className="border w-16 flex grow justify-center items-center"
                                            style={{ borderColor: 'rgba(25, 118, 210, 0.5)' }}
                                        >
                                            {fontSize}
                                        </div>
                                        <Button
                                            onClick={(e) => {
                                                setFontSize((prev) => {
                                                    if (prev <= 42) {
                                                        localStorage.setItem('fontSize', prev + 2);
                                                        return prev + 2;
                                                    }
                                                    return prev;
                                                });
                                            }}
                                            variant="outlined"
                                            color="primary"
                                            sx={{ borderRadius: '0 20px 20px 0' }}
                                        >
                                            +
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        </Modal>

                        <DownloadModal
                            position={modalPosition}
                            open={openDownload}
                            setOpen={setOpenDownload}
                            chapter={chapterData?.data}
                        />
                    </div>
                </div>
            )}
        </>
    );
}

export default ReadingChapter;
