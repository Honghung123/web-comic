import { useContext, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { DndContext, closestCorners } from '@dnd-kit/core';
import { SortableContext, arrayMove, verticalListSortingStrategy } from '@dnd-kit/sortable';
import SettingsRoundedIcon from '@mui/icons-material/SettingsRounded';
import Tippy from '@tippyjs/react';

import { Context, UPDATE_PRIORITY } from '../GlobalContext';
import ComicSourceItem from './ComicSourceItem';

function ComicSources() {
    const { servers, serversDispatch, currentPage } = useContext(Context);
    const [tempServers, setTempServers] = useState(servers);
    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        setSearchParams((prev) => {
            prev.delete('page');
            if (currentPage === '') {
                prev.delete('genre');
            }
            return prev;
        });
    }, [servers]);

    const getPosById = (id) => servers.findIndex((server) => server.id === id);
    const handleChangePriority = (e) => {
        const { active, over, delta } = e;
        const oldPos = getPosById(active.id);
        const newPos = getPosById(over?.id || servers[0].id);
        // click
        if (delta.x === 0 && delta.y === 0) {
            setTempServers((prev) => {
                return arrayMove(prev, oldPos, 0);
            });
            serversDispatch({
                type: UPDATE_PRIORITY,
                payload: { oldPos, newPos: 0 },
            });
            return;
        }
        // drag
        if (active.id === over.id) {
            return;
        }
        setTempServers((prev) => {
            return arrayMove(prev, oldPos, newPos);
        });
        serversDispatch({
            type: UPDATE_PRIORITY,
            payload: { oldPos, newPos },
        });
    };

    return (
        <div className="border-2 rounded-lg mx-auto py-4">
            <div className="text-2xl font-semibold text-center mr-4">
                <Tippy
                    content="Hãy kéo thả các nguồn truyện để thay đổi mức độ ưu tiên theo ý của bạn. Độ ưu tiên theo thứ tự giảm dần từ trên xuống."
                    trigger="click"
                >
                    <SettingsRoundedIcon
                        className="hover:cursor-pointer"
                        sx={{ marginBottom: 0.5, marginRight: 0.5 }}
                    />
                </Tippy>
                Nguồn truyện
            </div>
            <DndContext collisionDetection={closestCorners} onDragEnd={handleChangePriority}>
                <div className="mx-auto flex flex-wrap justify-center gap-4 mt-4 w-[180px]">
                    <SortableContext items={tempServers} strategy={verticalListSortingStrategy}>
                        {tempServers.map((server, index) => {
                            return <ComicSourceItem key={index} index={index} server={server} />;
                        })}
                    </SortableContext>
                </div>
            </DndContext>
        </div>
    );
}

export default ComicSources;
