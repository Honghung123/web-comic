import { FormControl, MenuItem, Select, InputLabel, TextField, Button, Divider } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { useContext, useEffect, useState } from 'react';

import * as request from '../../utils';
import { Context } from '../../GlobalContext';
import { useSearchParams } from 'react-router-dom';

function SearchBox() {
    const { servers, serversDispatch, keyword, setKeyword, genre, setGenre } = useContext(Context);
    const [searchParams, setSearchParams] = useSearchParams();
    const [listGenres, setListGenres] = useState([
        {
            tag: 'all',
            label: 'Tất cả',
            fullTag: '',
        },
    ]);
    const [tempKeyword, setTempKeyword] = useState('');

    const handleGenreChange = (e) => {
        if (e.target.value === 'all') {
            setGenre('');
            setSearchParams((prev) => {
                prev.delete('genre');
                return prev;
            });
            return;
        }
        setSearchParams((prev) => {
            prev.set('genre', e.target.value);
            return prev;
        });
        setGenre(e.target.value);
    };
    const handleKeywordChange = (e) => {
        setTempKeyword(e.target.value);
    };
    const handleSubmit = (e) => {
        if (tempKeyword === '') {
            setSearchParams((prev) => {
                prev.delete('keyword');
                prev.delete('page');
                return prev;
            });
        } else {
            setSearchParams((prev) => {
                prev.set('keyword', tempKeyword);
                prev.delete('page');
                return prev;
            });
        }
        setKeyword(tempKeyword);
    };

    useEffect(() => {
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
                    value={tempKeyword}
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
