import { Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import { useContext, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import ArrowUpwardRoundedIcon from '@mui/icons-material/ArrowUpwardRounded';
import ArrowDownwardRoundedIcon from '@mui/icons-material/ArrowDownwardRounded';

import { Context, DOWN_PRIORITY, UP_PRIORITY, UP_HIGHEST_PRIORITY } from '../../GlobalContext';

function ComicSources() {
    const { servers, serversDispatch } = useContext(Context);
    const [tempState, setTempState] = useState(0);
    const [searchParams, setSearchParams] = useSearchParams();

    const setHighestPriority = (e) => {
        const index = Number(e.target.title);
        serversDispatch({
            type: UP_HIGHEST_PRIORITY,
            payload: index,
        });
    };

    useEffect(() => {
        setSearchParams((prev) => {
            prev.delete('page');
            prev.delete('genre');
            return prev;
        });
    }, [servers]);

    return (
        <div className="w-full h-full" style={{ paddingTop: 82, paddingLeft: 20, paddingRight: -20 }}>
            <div className="border-2 rounded-lg mx-auto w-full">
                <div className="relative">
                    <h2 className="text-2xl text-center pt-8">Nguồn truyện</h2>
                    <div className="absolute top-0 right-0">
                        <SettingsIcon />
                    </div>
                </div>
                <div className="flex flex-wrap justify-center gap-4 mt-4 pb-4">
                    {servers.map((server, index) => {
                        return (
                            <div className="flex pl-2">
                                <Button
                                    title={`${index}`}
                                    key={server.id}
                                    variant="contained"
                                    color={index === 0 ? 'secondary' : 'primary'}
                                    onClick={setHighestPriority}
                                    sx={
                                        index === 0
                                            ? { borderRadius: 2, boxShadow: 'none', maxWidth: 130 }
                                            : {
                                                  borderRadius: 2,
                                                  boxShadow: 'none',
                                                  maxWidth: 130,
                                                  backgroundColor: '#D9D9D9',
                                                  '&:hover': {
                                                      backgroundColor: 'rgba(155, 86, 244, 0.5)',
                                                  },
                                              }
                                    }
                                >
                                    {server.name}
                                </Button>
                                <div className="ml-2">
                                    <div
                                        title={`${index}`}
                                        className="cursor-pointer hover:text-purple-500 text-gray-300"
                                        onClick={() => {
                                            if (index !== 0) {
                                                serversDispatch({
                                                    type: UP_PRIORITY,
                                                    payload: index,
                                                });
                                                setTimeout(() => {
                                                    setTempState((prev) => prev + 1);
                                                }, 100);
                                            }
                                        }}
                                    >
                                        <ArrowUpwardRoundedIcon />
                                    </div>
                                    <div
                                        title={`${index}`}
                                        className="cursor-pointer hover:text-purple-500 text-gray-300"
                                        onClick={() => {
                                            if (index < servers.length - 1) {
                                                serversDispatch({
                                                    type: DOWN_PRIORITY,
                                                    payload: index,
                                                });
                                                setTimeout(() => {
                                                    setTempState((prev) => prev + 1);
                                                }, 100);
                                            }
                                        }}
                                    >
                                        <ArrowDownwardRoundedIcon />
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}

export default ComicSources;
