import axios from 'axios';
import { useEffect, useState } from 'react';
import Divider from '@mui/material/Divider';
import FormControlLabel from '@mui/material/FormControlLabel';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Button from '@mui/material/Button';
import FileDownloadIcon from '@mui/icons-material/FileDownload';

import { toast } from 'react-toastify';

function DownloadModal({ open, setOpen, chapter }) {
    const [converters, setConverters] = useState([]);
    const [currentConverterId, setCurrentConverterId] = useState();
    const [loading, setLoading] = useState(false);
    console.log(converters);

    const handleDownload = async (e) => {
        setLoading(true);
        const payload = {
            title: chapter?.chapterTitle || '',
            content: chapter?.content || '',
        };
        const url = `${process.env.REACT_APP_API_URL}/comic/export-file`;
        try {
            const response = await axios.post(url, payload, {
                params: {
                    converter_id: currentConverterId,
                },
                headers: {
                    list_exporters: JSON.stringify(converters.map((converter) => converter.id)),
                },
                responseType: 'blob',
            });
            const converter = converters.find((converter) => {
                console.log(converter.id, currentConverterId);
                return converter.id == currentConverterId;
            });
            const blob = new Blob([response.data], {
                type: converter.blobType,
            });
            const windowUrl = window.URL || window.webkitURL;
            const downloadUrl = windowUrl.createObjectURL(blob);
            const anchor = document.createElement('a');
            anchor.href = downloadUrl;
            anchor.download =
                (`Chương ${chapter.chapterNumber}: ${chapter.chapterTitle}` || 'Untitled') +
                `.${converter.name.toLowerCase()}`;
            document.body.appendChild(anchor);
            anchor.click();
            // Xoa URL di sau khi tai xuong
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
        if (open) {
            axios
                .get(`${process.env.REACT_APP_API_URL}/comic/converter-plugins`)
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
        }
    }, [open]);

    return (
        <div className="bg-white rounded p-4 shadow-[0_0_10px_rgba(0,0,0,0.4)] w-[380px]">
            <div className="text-xl font-semibold">Tải xuống:</div>
            <Divider orientation="horizontal" className="h-2" />
            <RadioGroup
                aria-labelledby="file-types-choices"
                value={currentConverterId}
                onChange={(e) => {
                    setCurrentConverterId(e.target.value);
                }}
                sx={{ display: 'flex', flexDirection: 'row' }}
            >
                {converters &&
                    converters.map((converter) => {
                        return (
                            <FormControlLabel
                                key={converter.id}
                                value={converter.id}
                                control={<Radio color="secondary" />}
                                label={converter.name}
                                sx={{ width: 110 }}
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
    );
}

export default DownloadModal;
