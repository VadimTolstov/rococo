import {Errors} from "$lib/types/Errors";

export const validateImage = (src: File) => {
    return src.size > 1_048_576 ? Errors.IMAGE_CONSTRAINT_TOO_BIG : "";
}