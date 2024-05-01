import React, { useState } from 'react';
import axios from 'axios';

import SettingServer from '../../components/SettingServer/SettingServer';
import ListBook from '../../components/ListBook/ListBook';

export default function Home() {
    return (
        <div className="xl:px-48 lg:px-36 sm:px-8 py-8">
            <SettingServer />
            <ListBook></ListBook>
        </div>
    );
}
