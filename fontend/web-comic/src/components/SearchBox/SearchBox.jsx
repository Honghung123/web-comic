import { FormControl, MenuItem, Select, InputLabel, TextField, Button, Divider } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { useContext, useEffect, useState } from 'react';

import { Context } from '../GlobalContext';
import { useSearchParams, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';

function SearchBox() {
    const navigate = useNavigate();
    const location = useLocation();
    const { pathname } = location;
    const { servers, currentPage } = useContext(Context);
    const [searchParams, setSearchParams] = useSearchParams();
    const [genre, setGenre] = useState('');
    const [keyword, setKeyword] = useState('');
    const [listGenres, setListGenres] = useState([
        {
            tag: 'all',
            label: 'Tất cả',
            fullTag: '',
        },
    ]);

    const handleGenreChange = (e) => {
        if (e.target.value === 'all') {
            setSearchParams((prev) => {
                prev.delete('genre');
                prev.delete('page');
                return prev;
            });
            setGenre('');
        } else {
            setSearchParams((prev) => {
                prev.set('genre', e.target.value);
                prev.delete('page');
                return prev;
            });
            setGenre(e.target.value);
        }
        if (pathname !== '/') {
            navigate(`/?genre=${e.target.value}`);
        }
    };
    const handleKeywordChange = (e) => {
        setKeyword(e.target.value);
    };
    const handleSubmit = (e) => {
        setSearchParams((prev) => {
            if (keyword === '') {
                prev.delete('keyword');
            } else {
                prev.set('keyword', keyword);
            }
            prev.delete('page');
            return prev;
        });

        if (pathname !== '/') {
            navigate(`/?keyword=${keyword}`);
        }
    };

    useEffect(() => {
        setGenre('');
        if (servers && servers.length > 0) {
            const server_id = servers[0].id;
            axios
                .get(`${process.env.REACT_APP_API_URL}/comic/genres`, {
                    params: {
                        server_id,
                    },
                    headers: {
                        list_crawlers: JSON.stringify(servers.map((server) => server.id)),
                    },
                })
                .then((response) => {
                    const responseData = response.data;
                    if (responseData.statusCode === 200) {
                        responseData.data.unshift({
                            tag: 'all',
                            fullTag: '',
                            label: 'Tất cả',
                        });
                        setListGenres(responseData.data);
                        localStorage.setItem('genres', JSON.stringify(responseData.data));
                    } else {
                        // Thong bao loi
                        console.log(responseData.message);
                        toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
                    }
                })
                .catch((err) => {
                    console.log(err);
                    if (err.response?.status === 400) {
                        // back end update list servers
                        toast.error('Hệ thống đã cập nhật. Vui lòng tải lại trang!', { toastId: 400 });
                    } else {
                        toast.error('Có lỗi xảy ra. Vui lòng thử lại sau!', { toastId: 500 });
                    }
                });
        }
    }, [servers]);

    useEffect(() => {
        if (currentPage !== '') {
            setKeyword('');
            setGenre('');
        }
    }, [currentPage]);

    useEffect(() => {
        if (!searchParams.get('keyword')) {
            setKeyword('');
        }
    }, [searchParams.get('keyword')]);

    useEffect(() => {
        if (!searchParams.get('genre')) {
            setGenre('');
        }
    }, [searchParams.get('genre')]);

    return (
        <div className="flex m-[20px]">
            <FormControl className="lg:w-[120px] w-[100px]">
                <InputLabel id="genres-label">Thể loại</InputLabel>
                <Select
                    labelId="genres-label"
                    id="genres-input"
                    label="Thể loại"
                    value={genre}
                    onChange={handleGenreChange}
                    className="bg-white"
                    sx={{
                        borderRadius: '20px 0 0 20px',
                        '&.MuiOutlinedInput-root': {
                            '& fieldset': {
                                border: 'none',
                            },
                            '&:hover fieldset': {
                                border: 'none',
                            },
                            '&.Mui-focused fieldset': {
                                border: 'none',
                            },
                        },
                    }}
                >
                    {listGenres.map((genreItem, index) => (
                        <MenuItem value={genreItem.tag} key={index}>
                            {genreItem.label}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>

            <Divider variant="middle" flexItem orientation="vertical"></Divider>

            <FormControl className="lg:w-[400px] md:w-[300px]">
                <TextField
                    id="keyword-input"
                    value={keyword}
                    onChange={handleKeywordChange}
                    placeholder="Tìm kiếm theo tên truyện, tên tác giả"
                    variant="outlined"
                    className="bg-white line-clamp-1"
                    sx={{
                        '& .MuiOutlinedInput-root': {
                            '& fieldset': {
                                border: 'none',
                            },
                            '&:hover fieldset': {
                                border: 'none',
                            },
                            '&.Mui-focused fieldset': {
                                border: 'none',
                            },
                            '& input::placeholder': {
                                opacity: 0.5,
                                transition: 'opacity 0.3s',
                            },
                            '& input:focus::placeholder': {
                                opacity: 0,
                            },
                        },
                    }}
                />
            </FormControl>

            <FormControl className="lg:w-[80px] md:w-[70px]">
                <Button
                    variant="contained"
                    onClick={handleSubmit}
                    sx={{
                        height: 56,
                        backgroundColor: 'rgba(155, 86, 244, 0.5)',
                        borderRadius: '0 20px 20px 0',
                        boxShadow: 'none',
                        '&:hover': {
                            backgroundColor: 'rgba(155, 86, 244, 0.7)',
                            transform: 'none',
                            transition: 'none',
                            boxShadow: 'none',
                        },
                    }}
                >
                    <SearchIcon></SearchIcon>
                </Button>
            </FormControl>
        </div>
    );
}

export default SearchBox;
