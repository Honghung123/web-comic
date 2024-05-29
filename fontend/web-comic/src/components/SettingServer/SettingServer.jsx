import { Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Tippy from '@tippyjs/react';

import { Context } from '../../GlobalContext';

function SettingServer({ serverId, setServerId }) {
    const { servers } = useContext(Context);

    const handleChangeServer = (e) => {
        setServerId(e.target.id);
    };

    return (
        <div className="flex mt-2 text-xl font-semibold items-end">
            <h2>Nguồn truyện: </h2>
            <div className="flex flex-wrap justify-center">
                {servers.map((server, index) => {
                    return (
                        <Tippy key={server.id} content={server.name}>
                            <div
                                id={server.id}
                                className={`rounded-md text-lg text-center ml-4 cursor-pointer ${
                                    server.id === serverId ? 'bg-purple-400 text-white' : 'bg-gray-200'
                                }`}
                                style={{ padding: '4px 12px' }}
                                onClick={handleChangeServer}
                            >
                                #{index + 1}
                            </div>
                        </Tippy>
                    );
                })}
            </div>
        </div>
    );
}

export default SettingServer;
