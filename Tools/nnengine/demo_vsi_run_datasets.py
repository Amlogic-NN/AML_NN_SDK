import numpy as np
from PIL import Image

# import sys
# sys.path.append('../../src_code/pc')
from amlnn import engine


def get_image_do_preprocess(path):
    img = Image.open(path).convert('RGB').resize((224, 224))
    img = np.array(img)
    img = img / 127.5

    data = np.expand_dims(img,axis=0)
    data = data / 0.007843137718737125 + 1
    data = np.array(data).astype(np.uint8)

    return data

def postprocess_top1_top5(predictions, test_num, actual_label, acc_num_top1, acc_num_top5):
    predictions = predictions.reshape(-1)
    top5_indices = np.argsort(predictions)[::-1][:5]
    
    if actual_label == top5_indices[0]:
        acc_num_top1 += 1
    if actual_label in top5_indices:
        acc_num_top5 += 1

    print(f"pic:{test_num}, label:{actual_label}, Top5 result:{top5_indices}")

    return acc_num_top1, acc_num_top5


if __name__ == "__main__":
    amlnn = engine.AMLNN(
        device_type='android',                                              # device type, ['android','linux']
        model_type='tensorflow',                                            # framework type, ['caffe','tensorflow','tflite','darknet','onnx','keras','pytorch']
        model_path='./model_file/DDK6.4.8.7/image_classify_99_S905D3.nb',   # The nb file path in the pc : image_classify_99_S905D3.nb
        model_input_size='1 224 224 3',                                     # model input shape info, if multi-input, you can use '#' to split
        model_input_format='uint8',                                         # model input layer's data type, ['int8','uint8','int16','float32'], if multi-input, you can use '#' to split
        output_type='fp32',                                                 # output_type info, ['fp32','raw'], raw: raw result, fp32: float32 result
        run_cycles=1,                                                       # inference cycles, default: 1, means you will call inference once
        need_runtime=False,                                                 # if need runtime info, ['True','False']
        loglevel='ERROR'                                                    # log level, ['DEBUG','INFO','WARNING','ERROR']
    )

    amlnn.init()

    dataset = "./dataset/aml_dataset_crop_inception/aml_val_4901_inception.txt"
    acc_num_top5 = 0
    acc_num_top1 = 0
    label = 0
    num = 0
    compensation = 1
    for data_line in open(dataset):
        num += 1
        data_line = data_line.replace("\n","")
        img_name = data_line.split(" ")[0]
        label = int(data_line.split(" ")[1])
        label += compensation

        input_data = get_image_do_preprocess(img_name)
        amlnn.set_input(input_data)

        output_data = amlnn.inference()

        acc_num_top1, acc_num_top5 = postprocess_top1_top5(output_data[0], num, label, acc_num_top1, acc_num_top5)

    print(f"num:{num}")
    print(f"Top5 right num:{acc_num_top5}, Top5:{acc_num_top5/float(num)}")
    print(f"Top1 right num:{acc_num_top1}, Top1:{acc_num_top1/float(num)}")

    amlnn.destroy()