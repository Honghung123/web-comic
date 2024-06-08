import { Button } from '@mui/material';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

function ComicSourceItem({ index, server }) {
    const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id: server.id });

    const style = {
        transition,
        transform: CSS.Transform.toString(transform),
    };

    return (
        <div ref={setNodeRef} {...attributes} {...listeners} style={style} key={server.id}>
            <Button
                variant="contained"
                color={index === 0 ? 'secondary' : 'primary'}
                sx={
                    index === 0
                        ? { borderRadius: 2, maxWidth: 120 }
                        : {
                              borderRadius: 2,
                              maxWidth: 120,
                              backgroundColor: '#D9D9D9',
                              '&:hover': {
                                  backgroundColor: '#D9D9D9',
                              },
                          }
                }
            >
                {server.name}
            </Button>
        </div>
    );
}

export default ComicSourceItem;
