import { Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import { useContext } from 'react';
import { useSearchParams } from 'react-router-dom';

import { Context, UPDATE_PRIORITY } from '../../GlobalContext';

function SettingServer() {
    const { servers, serversDispatch } = useContext(Context);
    const [searchParams, setSearchParams] = useSearchParams();

    const handleChangeServer = (e) => {
        const newHighestPriority = servers.find((server) => server.id === e.target.id);
        setSearchParams((prev) => {
            prev.delete('page');
            prev.delete('genre');
            return prev;
        });
        serversDispatch({
            type: UPDATE_PRIORITY,
            payload: newHighestPriority,
        });
    };

    return (
        <div className="border-2 rounded-lg mx-auto p-4 pt-2" style={{ maxWidth: 500 }}>
            <div className="flex">
                <h2 className="text-2xl flex-1 text-center">Danh sách server</h2>
                <SettingsIcon />
            </div>
            <div className="flex flex-wrap justify-center gap-8 mt-4">
                {servers.map((server) => {
                    if (server.priority === 1) {
                        return (
                            <Button
                                id={server.id}
                                key={server.id}
                                className="w-1/4"
                                sx={{
                                    borderRadius: 2,
                                    boxShadow: 'none',
                                }}
                                color="secondary"
                                variant="contained"
                            >
                                {server.name}
                            </Button>
                        );
                    }

                    return (
                        <Button
                            onClick={handleChangeServer}
                            id={server.id}
                            key={server.id}
                            className="w-1/4"
                            variant="contained"
                            sx={{
                                borderRadius: 2,
                                boxShadow: 'none',
                                backgroundColor: '#D9D9D9',
                                '&:hover': {
                                    backgroundColor: 'rgba(155, 86, 244, 0.5)',
                                },
                            }}
                        >
                            {server.name}
                        </Button>
                    );
                })}
            </div>
        </div>
    );
}

export default SettingServer;
