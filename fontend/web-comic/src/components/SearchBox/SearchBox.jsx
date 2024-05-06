import { FormControl, MenuItem, Select, InputLabel, TextField, Button, Divider } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { useContext, useEffect, useState } from 'react';

import * as request from '../../utils';
import { Context } from '../../GlobalContext';
import { useSearchParams, useNavigate, useLocation } from 'react-router-dom';

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
            const server_id = servers.find((server) => server.priority === 1).id;
            request
                .get('/api/v1/comic/genres', {
                    params: {
                        server_id,
                    },
                })
                .then((result) => {
                    if (result.statusCode === 200) {
                        result.data.unshift({
                            tag: 'all',
                            fullTag: '',
                            label: 'Tất cả',
                        });
                        setListGenres(result.data);
                    } else {
                        //Thong bao loi
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

    return (
        <div className="flex" style={{ margin: 20 }}>
            <FormControl sx={{ minWidth: 120 }}>
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

            <FormControl>
                <Divider orientation="vertical"></Divider>
            </FormControl>

            <FormControl sx={{ minWidth: 400 }}>
                <TextField
                    id="keyword-input"
                    value={keyword}
                    onChange={handleKeywordChange}
                    placeholder="Tìm kiếm theo tên truyện, tên tác giả"
                    variant="outlined"
                    className="bg-white"
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
                        },
                    }}
                />
            </FormControl>

            <FormControl sx={{ minWidth: 80 }}>
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
