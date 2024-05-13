import React, { useContext, useState } from 'react';

import { Context } from '../../GlobalContext';
import SettingServer from '../../components/SettingServer';
import ReadingChapter from '../../components/ReadingChapter';

export default function Reading() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('/reading');

    return (
        <>
            <div className="px-4 py-8">
                <SettingServer />
                <ReadingChapter />
            </div>
        </>
    );
}
