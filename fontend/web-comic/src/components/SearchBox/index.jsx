import { FormControl, MenuItem, Select, InputLabel, TextField, Button, Divider } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { useContext, useEffect, useState } from 'react';

import * as request from '../../utils';
import { Context } from '../../GlobalContext';

function SearchBox() {
    const [servers, setServers] = useContext(Context);
    const [genre, setGenre] = useState('');
    const [listGenres, setListGenres] = useState([
        {
            tag: 'all',
            label: 'Tất cả',
            fullTag: '',
        },
    ]);
    const [keyword, setKeyword] = useState('');

    const handleGenreChange = (e) => {
        setGenre(e.target.value);
    };
    const handleKeywordChange = (e) => {
        setKeyword(e.target.value);
    };

    useEffect(() => {
        console.log('use effect');

        if (servers && servers.length > 0) {
            const server_id = servers[0].id;
            request
                .get('/api/v1/comic/genres', {
                    params: {
                        server_id,
                    },
                })
                .then((result) => {
                    console.log('result: ', result);
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
        <div style={{ margin: 20 }}>
            <FormControl sx={{ minWidth: 120 }}>
                <InputLabel id="genres-label">Thể loại</InputLabel>
                <Select
                    labelId="genres-label"
                    id="genres-input"
                    label="Thể loại"
                    value={genre}
                    onChange={handleGenreChange}
                    sx={{
                        backgroundColor: '#fff',
                        borderRadius: '20px 0 0 20px',
                        outline: 'none',
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
                    sx={{
                        backgroundColor: '#fff',
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
                    sx={{
                        height: 56,
                        backgroundColor: 'rgba(155, 86, 244, 0.5)',
                        borderRadius: '0 20px 20px 0',
                        boxShadow: 'none',
                        '&:hover': {
                            backgroundColor: 'rgba(155, 86, 244, 0.5)',
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
