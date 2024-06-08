import { useContext } from 'react';

import { Context } from '../../components/GlobalContext';
import ReadingChapter from '../../components/ReadingChapter';

function Reading() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('/reading');

    return (
        <div className="px-4 py-8">
            <ReadingChapter />
        </div>
    );
}

export default Reading;
