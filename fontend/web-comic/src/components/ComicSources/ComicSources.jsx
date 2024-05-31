import SettingsIcon from '@mui/icons-material/Settings';
import { useContext, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { DndContext, closestCorners } from '@dnd-kit/core';
import { SortableContext, arrayMove, verticalListSortingStrategy } from '@dnd-kit/sortable';

import { Context, UPDATE_PRIORITY } from '../../GlobalContext';
import ComicSourceItem from './ComicSourceItem';

function ComicSources() {
    const { servers, serversDispatch } = useContext(Context);
    const [tempServers, setTempServers] = useState(servers);
    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        setSearchParams((prev) => {
            prev.delete('page');
            prev.delete('genre');
            return prev;
        });
    }, [servers]);

    const getPosById = (id) => servers.findIndex((server) => server.id === id);
    const handleChangePriority = (e) => {
        const { active, over, delta } = e;
        const oldPos = getPosById(active.id);
        const newPos = getPosById(over.id);
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
        <div className="h-full" style={{ marginLeft: -40, paddingRight: 30 }}>
            <div className="border-2 rounded-lg mx-auto w-full pb-4">
                <div className="relative">
                    <div className="text-2xl font-semi-bold text-center hidden sm:flex items-center justify-center mt-4">
                        <SettingsIcon className="cursor-pointer" />
                        <h2>Nguồn truyện</h2>
                    </div>
                </div>
                <DndContext collisionDetection={closestCorners} onDragEnd={handleChangePriority}>
                    <div className="sm:flex flex-wrap justify-center gap-4 mt-4 pb-4 hidden" style={{ width: 190 }}>
                        <SortableContext items={tempServers} strategy={verticalListSortingStrategy}>
                            {tempServers.map((server, index) => {
                                return <ComicSourceItem key={index} index={index} server={server} />;
                            })}
                        </SortableContext>
                    </div>
                </DndContext>
            </div>
        </div>
    );
}

export default ComicSources;
