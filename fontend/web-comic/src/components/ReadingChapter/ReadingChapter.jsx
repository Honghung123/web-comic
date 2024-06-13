import { Button } from '@mui/material';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import MenuRoundedIcon from '@mui/icons-material/MenuRounded';
import SettingsRoundedIcon from '@mui/icons-material/SettingsRounded';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import CheckRoundedIcon from '@mui/icons-material/CheckRounded';
import NavigateNextRoundedIcon from '@mui/icons-material/NavigateNextRounded';
import NavigateBeforeRoundedIcon from '@mui/icons-material/NavigateBeforeRounded';
import Divider from '@mui/material/Divider';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import Tippy from '@tippyjs/react/headless';
import 'tippy.js/dist/tippy.css';

import { Context } from '../GlobalContext';
import ListChapters from '../ListChapters';
import SettingServer from '../SettingServer';
import Loading from '../Loading';
import * as Utils from '../../utils';
import DownloadModal from './DownloadModal';
import { fontFamilies, bgColors, lineHeights } from './constants';

function ReadingChapter() {
    const location = useLocation();
    const { pathname } = location;
    const navigate = useNavigate();
    const tempStr = pathname.substring(pathname.indexOf('/', 1) + 1);
    const serverId = tempStr.substring(0, tempStr.indexOf('/'));
    const tagId = tempStr.substring(tempStr.indexOf('/') + 1, tempStr.lastIndexOf('/'));
    const chapter = tempStr.substring(tempStr.lastIndexOf('/') + 1);
    const { servers } = useContext(Context);

    // state for change comic's source
    const [serverIdState, setServerIdState] = useState(serverId);

    // state for modal
    const [openSetting, setOpenSetting] = useState(false);
    const [openListChapters, setOpenListChapters] = useState(false);
    const [openDownload, setOpenDownload] = useState(false);

    // state for custom
    const [bgColor, setBgColor] = useState(localStorage.getItem('bgColor') || 'bg-gray-100');
    const [fontFamily, setFontFamily] = useState(localStorage.getItem('fontFamily') || fontFamilies[0].value);
    const [lineHeight, setLineHeight] = useState(localStorage.getItem('lineHeight') || '150%');
    const [fontSize, setFontSize] = useState(Number(localStorage.getItem('fontSize') || '20'));

    const [chapterData, setChapterData] = useState();
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    // fetch data
    useEffect(() => {
        setOpenListChapters(false);
        if (serverId) {
            setLoading(true);
            if (chapterData) {
                setChapterData({ ...chapterData, data: { title: chapterData?.data?.title } });
                // setChapterData(undefined);
            }

            axios
                .get(`${process.env.REACT_APP_API_URL}/comic/reading/${tagId}/chapters/${chapter}`, {
                    params: {
                        server_id: serverId,
                    },
                    headers: {
                        list_crawlers: JSON.stringify(servers.map((server) => server.id)),
                    },
                })
                .then((response) => {
                    console.log('chapter response: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        // save chapter into history
                        Utils.addChapter(chapter, tagId, serverId);
                        Utils.addComic(tagId, responseData.data.title, serverId);
                        setChapterData({
                            data: responseData.data,
                            pagination: responseData.pagination,
                        });
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
    }, [pathname]);

    console.log('chapterdata: ', chapterData);
    // change server when reading
    useEffect(() => {
        if (chapterData?.data && servers && servers.length > 0) {
            const { id: server_id, name: server_name } = servers.find((server) => server.id === serverIdState);

            console.log('post body: ', {
                title: chapterData.data.title,
                authorName: chapterData.data.author?.name,
                comicTagId: chapterData.data.comicTagId,
                chapterNumber: chapterData.data.chapterNo || 1,
            });

            const fecthData = async () => {
                try {
                    const response = await axios.post(
                        `${process.env.REACT_APP_API_URL}/comic/reading/change-server-chapter-content`,
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
                            headers: {
                                list_crawlers: JSON.stringify(servers.map((server) => server.id)),
                            },
                        },
                    );
                    console.log('change server: ', response);
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        // toast.success('Data fetched successfully!');
                        navigate(
                            `/reading/${serverIdState}/${responseData.data.comicTagId}/${responseData.pagination.currentPage}`,
                        );
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

            if (serverIdState !== serverId) {
                toast.promise(fecthData(), {
                    pending: `Chuyển sang server ${server_name}`,
                    success: 'Chuyển server thành công',
                    error: `Không tìm thấy truyện trên ${server_name}`,
                });
            }
        }
    }, [serverIdState]);

    return (
        <>
            {!chapterData ? (
                <div className="min-h-96 mt-16 mx-auto relative max-w-[1200px]">
                    <Loading loading={loading} />
                </div>
            ) : (
                <div className="min-h-96 mx-auto max-w-[1200px]">
                    <h1 className="text-3xl text-center font-semibold h-[36px]">{chapterData.data.title}</h1>
                    <h1 className="text-xl text-center font-semibold mt-2 text-gray-500 h-[36px]">
                        {chapterData.data.chapterNumber &&
                            `Chương ${chapterData.data.chapterNumber}: ${chapterData.data.chapterTitle}`}
                    </h1>
                    <div className="flex justify-center gap-4 my-4">
                        <Button
                            disabled={!chapterData.pagination.link.prevPage}
                            component={Link}
                            to={`/reading/${serverId}/${tagId}/${chapterData.pagination.link.prevPage}`}
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
                                    ? `/reading/${serverId}/${tagId}/${chapterData.pagination.link.nextPage}`
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

                    <SettingServer serverId={serverIdState} setServerId={setServerIdState} />

                    {/* content + setting properties */}
                    <div className="w-full mx-auto md:px-16 mt-4 relative">
                        <Loading loading={loading} />

                        <div
                            className={`${bgColor}${
                                bgColor === 'bg-white' ? ' border-2' : ''
                            } md:absolute z-50 top-0 left-0 divide-black rounded flex w-40 px-4 md:px-0 md:w-auto md:block mb-4`}
                        >
                            {/* List Chapters */}
                            <Tippy
                                interactive
                                visible={openListChapters}
                                onClickOutside={() => setOpenListChapters(false)}
                                placement="right-start"
                                render={(attrs) => (
                                    <div
                                        className="h-64 overflow-auto bg-white rounded p-4 shadow-[0_0_10px_rgba(0,0,0,0.4)] w-[380px]"
                                        tabIndex={-1}
                                        {...attrs}
                                    >
                                        <ListChapters headerSize="text-xl" tagId={tagId} serverId={serverId} />
                                    </div>
                                )}
                            >
                                <div
                                    onClick={() => setOpenListChapters(true)}
                                    className={`flex justify-center items-center cursor-pointer w-[50px] h-[50px] ${
                                        openListChapters ? 'text-purple-500' : ''
                                    }`}
                                >
                                    <MenuRoundedIcon />
                                </div>
                            </Tippy>

                            <Divider orientation="horizontal" variant="middle" />

                            {/* Setting properties */}
                            <Tippy
                                interactive
                                visible={openSetting}
                                onClickOutside={() => {
                                    if (!document.getElementById('menu-')) {
                                        setOpenSetting(false);
                                    }
                                }}
                                placement="right-start"
                                offset={[-50, 10]}
                                render={(attrs) => (
                                    <div
                                        tabIndex={-1}
                                        {...attrs}
                                        className="bg-white rounded p-4 shadow-[0_0_10px_rgba(0,0,0,0.4)] w-[380px]"
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
                                                            }  h-8 w-8 customized-cursor text-center`}
                                                        >
                                                            {bgColor === color && <CheckRoundedIcon />}
                                                        </div>
                                                    );
                                                })}
                                            </div>
                                        </div>

                                        <div className="flex justify-between gap-8 mt-4 items-center">
                                            <div className="w-[90px]">Font chữ</div>
                                            <Select
                                                onChange={(e) => {
                                                    localStorage.setItem('fontFamily', e.target.value);
                                                    setFontFamily(e.target.value);
                                                }}
                                                sx={{
                                                    flex: 1,
                                                    height: 40,
                                                    '&.MuiOutlinedInput-root': {
                                                        '&:hover fieldset': {
                                                            borderColor: 'rgba(25, 118, 210, 0.5)',
                                                        },
                                                        '&.Mui-focused fieldset': {
                                                            border: '1px solid rgba(25, 118, 210, 0.5)',
                                                        },
                                                    },
                                                }}
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
                                            <div className="w-[90px]">Cách dòng</div>
                                            <Select
                                                onChange={(e) => {
                                                    localStorage.setItem('lineHeight', e.target.value);
                                                    setLineHeight(e.target.value);
                                                }}
                                                sx={{
                                                    flex: 1,
                                                    height: 40,
                                                    '&.MuiOutlinedInput-root': {
                                                        '&:hover fieldset': {
                                                            borderColor: 'rgba(25, 118, 210, 0.5)',
                                                        },
                                                        '&.Mui-focused fieldset': {
                                                            border: '1px solid rgba(25, 118, 210, 0.5)',
                                                        },
                                                    },
                                                }}
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
                                            <div className="w-[90px]">Cỡ chữ</div>
                                            <div className="flex grow">
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
                                                <div className="border flex grow justify-center items-center border-[rgba(25,118,210,0.5)]">
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
                                )}
                            >
                                <div
                                    onClick={() => setOpenSetting(true)}
                                    className={`flex justify-center items-center cursor-pointer w-[50px] h-[50px] ${
                                        openSetting ? 'text-purple-500' : ''
                                    }`}
                                >
                                    <SettingsRoundedIcon />
                                </div>
                            </Tippy>
                            <Divider orientation="horizontal" variant="middle" />

                            {/* Download file */}
                            <Tippy
                                interactive
                                visible={openDownload}
                                onClickOutside={() => setOpenDownload(false)}
                                placement="right-start"
                                offset={[-100, 10]}
                                render={(attrs) => (
                                    <DownloadModal
                                        tabIndex={-1}
                                        {...attrs}
                                        open={openDownload}
                                        setOpen={setOpenDownload}
                                        chapter={chapterData?.data}
                                    />
                                )}
                            >
                                <div
                                    onClick={() => setOpenDownload(true)}
                                    className={`flex justify-center items-center cursor-pointer w-[50px] h-[50px] ${
                                        openDownload ? 'text-purple-500' : ''
                                    }`}
                                >
                                    <FileDownloadIcon />
                                </div>
                            </Tippy>
                        </div>

                        <div
                            className={`w-full min-h-96 rounded ${bgColor}${
                                bgColor === 'bg-white' ? ' border-2' : ''
                            } sm:p-4 p-2 text-slate-700`}
                            style={{ fontSize, lineHeight, fontFamily }}
                            dangerouslySetInnerHTML={{
                                __html: chapterData.data.content,
                            }}
                        ></div>
                        {/* <Modal
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
                        </Modal> */}

                        {/* <Modal
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
                                    <div style={{ width: 90 }}>Font chữ</div>
                                    <Select
                                        onChange={(e) => {
                                            localStorage.setItem('fontFamily', e.target.value);
                                            setFontFamily(e.target.value);
                                        }}
                                        sx={{
                                            flex: 1,
                                            height: 40,
                                            '&.MuiOutlinedInput-root': {
                                                '&:hover fieldset': {
                                                    borderColor: 'rgba(25, 118, 210, 0.5)',
                                                },
                                                '&.Mui-focused fieldset': {
                                                    border: '1px solid rgba(25, 118, 210, 0.5)',
                                                },
                                            },
                                        }}
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
                                    <div style={{ width: 90 }}>Cách dòng</div>
                                    <Select
                                        onChange={(e) => {
                                            localStorage.setItem('lineHeight', e.target.value);
                                            setLineHeight(e.target.value);
                                        }}
                                        sx={{
                                            flex: 1,
                                            height: 40,
                                            '&.MuiOutlinedInput-root': {
                                                '&:hover fieldset': {
                                                    borderColor: 'rgba(25, 118, 210, 0.5)',
                                                },
                                                '&.Mui-focused fieldset': {
                                                    border: '1px solid rgba(25, 118, 210, 0.5)',
                                                },
                                            },
                                        }}
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
                                    <div style={{ width: 90 }}>Cỡ chữ</div>
                                    <div className="flex grow">
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
                                            className="border flex grow justify-center items-center"
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
                        </Modal> */}
                    </div>
                    <div className="flex justify-center gap-4 my-4">
                        <Button
                            disabled={!chapterData.pagination.link.prevPage}
                            component={Link}
                            to={`/reading/${serverId}/${tagId}/${chapterData.pagination.link.prevPage}`}
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
                                    ? `/reading/${serverId}/${tagId}/${chapterData.pagination.link.nextPage}`
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
                </div>
            )}
        </>
    );
}

export default ReadingChapter;
