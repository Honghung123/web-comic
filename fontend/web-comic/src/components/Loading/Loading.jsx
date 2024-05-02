import CircularProgress from '@mui/material/CircularProgress';

function Loading({ disable = false }) {
    return (
        <>
            {!disable && (
                <div className="absolute w-full h-full bg-gray-200/90 z-20 flex justify-center items-center">
                    <CircularProgress color="secondary" />
                </div>
            )}
        </>
    );
}

export default Loading;
