import CircularProgress from '@mui/material/CircularProgress';

function Loading({ loading = false }) {
    return (
        <>
            {loading && (
                <div className="absolute w-full h-full bg-gray-100/90 z-50 left-0 flex justify-center items-center">
                    <CircularProgress color="secondary" />
                </div>
            )}
        </>
    );
}

export default Loading;
