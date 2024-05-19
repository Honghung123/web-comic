import axios from 'axios';
import { useEffect, useState } from 'react';
import Divider from '@mui/material/Divider';
import Modal from '@mui/material/Modal';
import FormControlLabel from '@mui/material/FormControlLabel';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Button from '@mui/material/Button';
import FileDownloadIcon from '@mui/icons-material/FileDownload';

import { toast } from 'react-toastify';

function DownloadModal({ position, open, setOpen, chapter }) {
    const [converters, setConverters] = useState([]);
    const [currentConverterId, setCurrentConverterId] = useState();
    const [loading, setLoading] = useState(false);

    const handleDownload = async (e) => {
        setLoading(true);
        const payload = {
            title: chapter?.chapterTitle || '',
            content: chapter?.content || '',
        };
        const url = `http://localhost:8080/api/v1/comic/export-file`;
        try {
            const response = await axios.post(url, payload, {
                params: {
                    converter_id: currentConverterId,
                },
                responseType: 'blob',
            });
            console.log('----------');
            console.log(response);
            const blob = new Blob([response.data], {
                type: converters.find((converter) => {
                    console.log(converter.id, currentConverterId);
                    return converter.id == currentConverterId;
                }).blobType,
            });
            const windowUrl = window.URL || window.webkitURL;
            const downloadUrl = windowUrl.createObjectURL(blob);
            const anchor = document.createElement('a');
            anchor.href = downloadUrl;
            anchor.download = chapter.chapterTitle || 'untitled';
            document.body.appendChild(anchor);
            anchor.click();
            // Xóa URL sau khi đã tải xuống
            window.URL.revokeObjectURL(downloadUrl);
        } catch (error) {
            console.log(error);
            console.log(error.response.status);
            throw error;
        } finally {
            setOpen(false);
            setLoading(false);
        }
    };

    const btnDownloadClick = () => {
        if (!loading) {
            toast.promise(handleDownload(), {
                pending: 'Đang download...',
                success: 'Download thành công!',
                error: 'Download thất bại, vui lòng thử lại sau.',
            });
        }
    };

    console.log('converters: ', converters);
    console.log('current: ', currentConverterId);

    useEffect(() => {
        axios
            .get(`http://localhost:8080/api/v1/comic/converter-plugins`)
            .then((response) => {
                const responseData = response.data;
                if (responseData.statusCode === 200) {
                    setConverters(responseData.data);
                    setCurrentConverterId(responseData.data[0]?.id);
                } else {
                    // Thong bao loi
                    console.log(responseData.message);
                }
            })
            .catch((err) => {
                // Thong bao loi
                console.log(err);
            });
    }, []);

    return (
        <Modal
            open={open}
            onClose={() => {
                setOpen(false);
            }}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
            BackdropProps={{
                sx: { backgroundColor: 'transparent' },
            }}
            sx={{ top: position.y, left: position.x }}
        >
            <div className="w-64 bg-white rounded p-4 shadow">
                <div className="text-xl font-semibold">Tải xuống:</div>
                <Divider orientation="horizontal" className="h-2" />
                <RadioGroup
                    aria-labelledby="file-types-choices"
                    value={currentConverterId}
                    onChange={(e) => {
                        setCurrentConverterId(e.target.value);
                    }}
                >
                    {converters &&
                        converters.map((converter) => {
                            return (
                                <FormControlLabel
                                    key={converter.id}
                                    value={converter.id}
                                    control={<Radio color="secondary" />}
                                    label={converter.name}
                                />
                            );
                        })}
                </RadioGroup>

                <div className="text-center">
                    <Button
                        variant="outlined"
                        color="success"
                        onClick={btnDownloadClick}
                        sx={{ opacity: loading ? 0.5 : 1 }}
                    >
                        <FileDownloadIcon />
                    </Button>
                </div>
            </div>
        </Modal>
    );
}

export default DownloadModal;
