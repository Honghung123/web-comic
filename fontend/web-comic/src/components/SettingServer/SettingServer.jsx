import { Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import { useContext } from 'react';

import { Context, UPDATE_LIST, UPDATE_PRIORITY } from '../../GlobalContext';

function SettingServer() {
    const { servers, serversDispatch } = useContext(Context);

    const handleChangeServer = (e) => {
        const newHighestPriority = Number(e.target.id[e.target.id.length - 1]);
        console.log(newHighestPriority);
        console.log(servers);
        serversDispatch({
            type: UPDATE_PRIORITY,
            payload: servers[newHighestPriority],
        });
    };

    return (
        <div className="w-2/3 lg:w-1/2 border-2 rounded-lg mx-auto p-4 pt-2">
            <div className="flex">
                <h2 className="text-2xl flex-1 text-center">Danh sach server</h2>
                <SettingsIcon />
            </div>
            <div className="flex justify-center gap-8 mt-4">
                {servers.map((item, index) => {
                    if (item.priority == 1) {
                        return (
                            <Button
                                onClick={handleChangeServer}
                                id={`server-${index}`}
                                key={index}
                                className="w-1/4"
                                sx={{
                                    borderRadius: 2,
                                    boxShadow: 'none',
                                }}
                                color="secondary"
                                variant="contained"
                            >
                                {item.name}
                            </Button>
                        );
                    }

                    return (
                        <Button
                            onClick={handleChangeServer}
                            id={`server-${index}`}
                            key={index}
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
                            {item.name}
                        </Button>
                    );
                })}
            </div>
        </div>
    );
}

export default SettingServer;
